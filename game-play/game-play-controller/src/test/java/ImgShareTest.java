import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImgShareTest {
    private BufferedImage image;
    private int imageWidth = 400;  //图片的宽度
    private int imageHeight = 600; //图片的高度
    private Font FONT_SONG_BOLD_16 = new Font("黑体", Font.BOLD, 16);
    private Font FONT_SONG_PLAIN_14 = new Font("黑体", Font.PLAIN, 14);

    public void createImage(String fileLocation) {
        BufferedOutputStream bos = null;
        if (image != null) {
            try {
                FileOutputStream fos = new FileOutputStream(fileLocation);
                bos = new BufferedOutputStream(fos);

                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
                encoder.encode(image);
                bos.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {//关闭输出流
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void graphicsGenerate(String mainPicUrl, String codeUrl) {
        //画布整体布局背景为白色
        image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D main = image.createGraphics();
        main.setColor(Color.white);
        main.fillRect(0, 0, imageWidth, imageHeight);

        //插入陪玩师主图
        int H_mainPic = 330;
        int mainPic2Top = 0;
        Graphics mainPic = image.getGraphics();
        BufferedImage bimg = null;
        try {
            bimg = javax.imageio.ImageIO.read(new java.io.File(mainPicUrl));
        } catch (Exception e) {
        }
        if (bimg != null) {
            mainPic.drawImage(bimg, 0, mainPic2Top, imageWidth, H_mainPic, null);
            mainPic.dispose();
        }

        //陪玩师个人信息区域
        int H_person = 40;
        int person2Top = mainPic2Top + H_mainPic + 10;
        String personContent = "泼辣小医仙";
        String ageContent = "♀22";
        Graphics2D person = image.createGraphics();
        person.setColor(Color.white);
        person.fillRect(0, person2Top, imageWidth, H_person);

        person.setColor(Color.black);
        person.setFont(FONT_SONG_BOLD_16);
        person.drawString(personContent, 0, person2Top + H_person / 2);
        int personLen = getContentLength(personContent,person);
        int ageLen = getContentLength(ageContent,person);
        person.setFont(FONT_SONG_PLAIN_14);
        person.setColor(new Color(0xFF1562));
        person.fillRect(personLen+10, person2Top+5, ageLen, H_person/2);
        person.setColor(Color.white);
        person.drawString(ageContent, personLen+10, person2Top + H_person / 2);

        //*******技能和技能标签区域
        int H_tech = 50;
        int tech2Top = person2Top + H_person + 5;
        String techStr = "王者荣耀陪玩 段位：永恒钻石 个人标签：御姐 娇小 声音标签：萝莉音 浪漫";
        Graphics2D tech = image.createGraphics();
        tech.setColor(Color.white);
        tech.fillRect(0, tech2Top, imageWidth, H_tech);
        //设置字体
        tech.setColor(Color.black);
        tech.setFont(FONT_SONG_PLAIN_14);
        int y = tech2Top + (H_tech) / 2 - 10;

        //文字叠加,自动换行叠加
        int tempX = 0;
        int tempY = y;
        int tempCharLen = 0;//单字符长度
        int tempLineLen = 0;//单行字符总长度临时计算
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < techStr.length(); i++) {
            char tempChar = techStr.charAt(i);
            tempCharLen = getCharLen(tempChar, tech);
            tempLineLen += tempCharLen;
            if (tempLineLen >= imageWidth) {
                //长度已经满一行,进行文字叠加
                tech.drawString(sb.toString(), tempX, tempY);
                sb.delete(0, sb.length());//清空内容,重新追加
                tempY += FONT_SONG_PLAIN_14.getSize()+5;
                tempLineLen = 0;
            }
            sb.append(tempChar);//追加字符
        }

        tech.drawString(sb.toString(), tempX, tempY);//最后叠加余下的文字
        tech.dispose();

        //画一条横线
        Graphics2D line = image.createGraphics();
        int lineWidth = imageWidth - 20;
        int line2Top = tech2Top + H_tech + 10;
        line.setColor(Color.DARK_GRAY);
        line.drawRect(10, line2Top, lineWidth, 0);

        //***********小程序码和文案
        int H_code = 130;
        int code2Top = line2Top + 10;
        int codeWidth = imageWidth / 3;
        int shareWidth = imageWidth - codeWidth;
        String shareStr1 = "泼辣小医仙正在申请陪玩师";
        String shareStr2 = "长按识别二维码认可她的能力";

        Graphics code = image.getGraphics();
        BufferedImage codeImg = null;
        try {
            codeImg = javax.imageio.ImageIO.read(new java.io.File(codeUrl));
        } catch (Exception e) {
        }
        if (codeImg != null) {
            code.drawImage(codeImg, 10, code2Top, codeWidth, H_code, null);
            code.dispose();
        }
        //TimesRoman, Courier, Arial
        Graphics2D share = image.createGraphics();
        share.setColor(Color.white);
        share.fillRect(codeWidth + 20, code2Top, shareWidth - 10, H_code);
        share.setColor(Color.BLACK);
        share.setFont(FONT_SONG_BOLD_16);
        share.drawString(shareStr1, codeWidth + 20, code2Top + (H_code / 2) - 10);
        share.setFont(FONT_SONG_PLAIN_14);
        share.drawString(shareStr2, codeWidth + 20, code2Top + (H_code / 2) + 10);
        createImage("E:\\test\\wzpl.jpg");
    }

    public int getCharLen(char c, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charWidth(c);
    }

    public int getContentLength(String content, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charsWidth(content.toCharArray(), 0, content.length());
    }

    public static void main(String[] args) {
        ImgShareTest test = new ImgShareTest();
        try {
            test.graphicsGenerate("E:\\test\\main_head_url.jpg", "E:\\test\\qrcode.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
