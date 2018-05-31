import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.GenderEnum;
import com.fulu.game.common.utils.GenIdUtil;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

public class ImgShareTest {
    private BufferedImage image;
    private int imageWidth = 690;  //图片的宽度
    private int imageHeight = 1146; //图片的高度
    private Font FONT_SONG_BOLD_16 = new Font("黑体", Font.BOLD, 16);
    private Font FONT_SONG_PLAIN_14 = new Font("黑体", Font.PLAIN, 14);

    private String EXT_NAME = "jpg";
    private static final int FONT_SIZE_24 = 24;
    private static final int FONT_SIZE_22 = 22;
    private Font FONT_SONG_BOLD = new Font("宋体", Font.BOLD, FONT_SIZE_24);
    private Font FONT_SONG_PLAIN = new Font("宋体", Font.PLAIN, FONT_SIZE_22);
    private static final Color WHITE = Color.white;
    private static final Color BLACK = Color.black;
    private static final Color FEMALE_COLOR = Color.magenta;
    private static final Color LIGHT_GRAY = Color.LIGHT_GRAY;

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

    //陪玩师技能名片分享
    public String techCardCreate() throws IOException {
        //画布整体布局背景为白色
        image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D main = image.createGraphics();
        main.setColor(WHITE);
        main.fillRect(0, 0, imageWidth, imageHeight);

        //插入陪玩师主图
        int H_mainPic = 700;
        int mainPic2Top = 0;
        Graphics mainPic = image.getGraphics();
        BufferedImage bimg = null;
        try {
            String mainPicUrl = "http://test-game-play.oss-cn-hangzhou.aliyuncs.com/2018/5/7/5848a297c8514b46b83d20b2818da8a8.jpg";
            //bimg = javax.imageio.ImageIO.read(new URL(contentMap.get("mainPicUrl")));
            bimg = javax.imageio.ImageIO.read(new URL(mainPicUrl));
        } catch (Exception e) {
        }
        if (bimg != null) {
            mainPic.drawImage(bimg, 0, mainPic2Top, imageWidth, H_mainPic, null);
            mainPic.dispose();
        }

        //陪玩师个人信息区域
        int H_person = 70;
        int person2Top = mainPic2Top + H_mainPic + 10;
        String personContent = "泼辣小医仙";
        Integer gender = 2;
        Integer age = 22;
        Color defaultColor = FEMALE_COLOR;
        String genderAndAge = "";
        if (GenderEnum.ASEXUALITY.getType() == gender || GenderEnum.MALE.getType() == gender) {
            genderAndAge = GenderEnum.SYMBOL_MALE.getMsg() + " " + age;
        } else {
            defaultColor = FEMALE_COLOR;
            genderAndAge = GenderEnum.SYMBOL_LADY.getMsg() + " " + age;
        }
        genderAndAge += "御姐 娇小 甜美 霸道";
        Graphics2D person = image.createGraphics();
        person.setColor(WHITE);
        person.fillRect(0, person2Top, imageWidth, H_person);

        person.setColor(BLACK);
        person.setFont(FONT_SONG_BOLD);
        person.drawString(personContent, 0, person2Top + H_person / 2);
        int personLen = getContentLength(personContent, person);
        int ageLen = getContentLength(genderAndAge, person);
        person.setFont(FONT_SONG_PLAIN);
        person.setColor(defaultColor);
        person.fillRect(personLen + 10, person2Top + 5, ageLen, H_person / 2);
        person.setColor(WHITE);
        person.drawString(genderAndAge, personLen + 10, person2Top + H_person / 2);

        //*******技能和技能标签区域
        int H_tech = 70;
        int tech2Top = person2Top + H_person + 5;
        int width1 = imageWidth/6;

        Graphics category_g = image.getGraphics();
        BufferedImage categoryImg = null;
        String categoryUrl = "http://test-game-play.oss-cn-hangzhou.aliyuncs.com/2018/5/7/84b0f75c8429460fa3e131d2c403c9cf.png";
        try {
            categoryImg = javax.imageio.ImageIO.read(new URL(categoryUrl));
        } catch (Exception e) {
        }
        if (categoryImg != null) {
            category_g.drawImage(categoryImg, 10, tech2Top, width1, H_tech, null);
            category_g.dispose();
        }

        String techAndTag = "王者荣耀：￥72元/小时 永恒钻石 中单";
        Graphics2D tech = image.createGraphics();
        tech.setColor(WHITE);
        tech.fillRect(width1+10, tech2Top, imageWidth-width1-10, H_tech);
        //设置字体
        tech.setColor(BLACK);
        tech.setFont(FONT_SONG_PLAIN);

//        //文字叠加,自动换行叠加
//        int tempX = 0;
//        int tempY = tech2Top;
//        int tempCharLen = 0;//单字符长度
//        int tempLineLen = 0;//单行字符总长度临时计算
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < techAndTag.length(); i++) {
//            char tempChar = techAndTag.charAt(i);
//            tempCharLen = getCharLen(tempChar, tech);
//            tempLineLen += tempCharLen;
//            if (tempLineLen >= imageWidth) {
//                //长度已经满一行,进行文字叠加
//                tech.drawString(sb.toString(), tempX, tempY);
//                sb.delete(0, sb.length());//清空内容,重新追加
//                tempY += FONT_SONG_PLAIN.getSize() + 10;
//                tempLineLen = 0;
//            }
//            sb.append(tempChar);//追加字符
//        }
        tech.drawString(techAndTag, width1+10, tech2Top+H_tech/2);
        tech.dispose();

        //画一条横线
        Graphics2D line = image.createGraphics();
        int lineWidth = imageWidth - 20;
        int line2Top = tech2Top + H_tech;
        line.setColor(LIGHT_GRAY);
        line.drawRect(10, line2Top, lineWidth, 0);

        //小程序码和文案
        int H_code = 200;
        int code2Top = line2Top + 10;
        int codeWidth = imageWidth / 3;
        int shareWidth = imageWidth - codeWidth;
        String shareContent = "今天好无聊哦，快来和我一起开黑吧~";
        Graphics code = image.getGraphics();
        BufferedImage codeImg = null;
        String codeUrl = "http://app-testing-new.oss-cn-hangzhou.aliyuncs.com/2018/5/31/9a4ce7a39a064d15b231f98062857b92.jpg";
        try {
            codeImg = javax.imageio.ImageIO.read(new URL(codeUrl));
        } catch (Exception e) {
        }
        if (codeImg != null) {
            code.drawImage(codeImg, 10, code2Top, codeWidth, H_code, null);
            code.dispose();
        }

        Graphics2D share = image.createGraphics();
        share.setColor(WHITE);
        share.fillRect(codeWidth + 30, code2Top, shareWidth - 10, H_code);
        share.setColor(BLACK);
        share.setFont(FONT_SONG_BOLD);
        share.drawString(shareContent, codeWidth + 30, code2Top + (H_code / 2) - 15);

//        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
//        ImageOutputStream imageOS = ImageIO.createImageOutputStream(byteArrayOS);
//        ImageIO.write(image, EXT_NAME, imageOS);
//        InputStream is = new ByteArrayInputStream(byteArrayOS.toByteArray());
//        String imgName = GenIdUtil.GetImgNo() + "." + EXT_NAME;
        createImage("E:\\test\\aaaaa.jpg");
        //return ossUtil.uploadFile(is, imgName);
        return null;

    }

    public static void main(String[] args) {
        ImgShareTest test = new ImgShareTest();
        try {
            test.techCardCreate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
