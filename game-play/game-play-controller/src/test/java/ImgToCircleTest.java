import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

/**
 * 将一个已知的图片剪裁出圆形部分
 */
public class ImgToCircleTest {

    /**
     * 绘制圆形图片
     * @throws IOException
     */
    public static void circle() throws IOException {
        String url = "http://test-game-play.oss-cn-hangzhou.aliyuncs.com/2018/5/7/84b0f75c8429460fa3e131d2c403c9cf.png";
        BufferedImage srcImg0 = ImageIO.read(new URL(url));
        //圆的外切正方形的边长
        int width = 60;
        // 透明底的图片
        BufferedImage formatImage = new BufferedImage(width, width, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = formatImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, width, width);
        g.setClip(shape);
        g.drawImage(srcImg0, 0, 0, width, width, null);
        g.dispose();

        //新创建一个graphics，这样画的圆不会有锯齿
        g = formatImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int border = 1;
        //画笔用法:线条宽度+线段类型+连接处形状:https://blog.csdn.net/li_tengfei/article/details/6098093
        Stroke s = new BasicStroke(0.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g.setStroke(s);
        //线的颜色
        g.setColor(Color.gray);
        g.drawOval(border, border, width - border * 2, width - border * 2);
        g.dispose();
        OutputStream os = new FileOutputStream("E:\\test\\z.png");
        ImageIO.write(formatImage, "PNG", os);
    }

    public static void roundCorner(int cornerRadius) throws IOException {
        String url = "http://test-game-play.oss-cn-hangzhou.aliyuncs.com/2018/5/7/5848a297c8514b46b83d20b2818da8a8.jpg";
        BufferedImage image = ImageIO.read(new URL(url));
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = output.createGraphics();
        output = g2.getDeviceConfiguration().createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        g2.dispose();
        g2 = output.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillRoundRect(0, 0,w, h, cornerRadius, cornerRadius);
        g2.setComposite(AlphaComposite.SrcIn);
        g2.drawImage(image, 0, 0, w, h, null);
        g2.dispose();
        OutputStream os = new FileOutputStream("E:\\test\\y.png");
        ImageIO.write(output, "jpg", os);
    }

    public static void main(String[] args) throws IOException {
        //roundCorner(40);
        circle();
    }
}
