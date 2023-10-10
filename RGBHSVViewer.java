import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;

public class RGBHSVViewer extends JFrame {
    private BufferedImage image; // 画像データを保持する変数
    private RGBImagePanel panel; // イメージを表示するためのパネル

    public RGBHSVViewer() {
        setTitle("RGB & HSV Viewer"); // ウィンドウのタイトルを設定
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ウィンドウを閉じるとプログラムが終了するように設定

        Image img = load(); // 画像をロードする
        if (img != null) {
            // ImageをBufferedImageに変換
            this.image = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
            this.image.getGraphics().drawImage(img, 0, 0, null); // 画像を描画

            setSize(image.getWidth(), image.getHeight()); // ウィンドウのサイズを画像のサイズに合わせる

            panel = new RGBImagePanel(this.image); // 画像表示用のパネルを初期化
            add(panel); // パネルをウィンドウに追加
        }
    }

    // 画像をロードするメソッド
    public Image load() {
        Image image = null;
        FileDialog l = new FileDialog(this, "Load", FileDialog.LOAD); // 画像選択ダイアログを表示
        l.setModal(true);
        l.setVisible(true);
        if (l.getFile() != null) {
            MediaTracker tracker = new MediaTracker(this); // メディアトラッカーで画像のロード状態を監視
            try {
                image = ImageIO.read(new File(l.getDirectory() + l.getFile())); // 画像をロード
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0); // エラー発生時は終了
            }
            tracker.addImage(image, 0);
            try {
                tracker.waitForID(0); // 画像のロードが完了するまで待つ
            } catch (InterruptedException e) {}
        }
        return image;
    }

    public static void main(String[] args) {
        RGBHSVViewer viewer = new RGBHSVViewer(); // ウィンドウを作成
        viewer.setVisible(true); // ウィンドウを表示
    }

    // 画像を表示するためのパネルクラス
    class RGBImagePanel extends JPanel {
        private BufferedImage image;

        public RGBImagePanel(BufferedImage image) {
            this.image = image;
            // マウスクリックのリスナーを追加
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int x = e.getX();
                    int y = e.getY();
                    // クリック位置が画像内である場合
                    if (x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight()) {
                        int rgb = image.getRGB(x, y); // クリック位置のRGB値を取得
                        int r = (rgb >> 16) & 0xFF; // 赤成分を取得
                        int g = (rgb >> 8) & 0xFF;  // 緑成分を取得
                        int b = rgb & 0xFF;         // 青成分を取得

                        float[] hsv = new float[3];
                        Color.RGBtoHSB(r, g, b, hsv); // RGBをHSVに変換

                        // RGBおよびHSV値をメッセージとして表示
                        JOptionPane.showMessageDialog(RGBHSVViewer.this,
                                "RGB: (" + r + ", " + g + ", " + b + ")\n" +
                                "HSV: (" + hsv[0] + ", " + hsv[1] + ", " + hsv[2] + ")");
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, this); // 画像をパネルに描画
        }
    }
}
