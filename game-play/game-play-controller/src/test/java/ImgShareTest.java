import com.fulu.game.common.enums.GenderEnum;
import com.fulu.game.common.utils.ImgUtil;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import lombok.Builder;
import lombok.Data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class ImgShareTest {
    private BufferedImage image;
    private int imageWidth = 690;  //图片的宽度
    private int imageHeight = 1146; //图片的高度

    private String EXT_NAME = "jpg";
    private static final int FONT_SIZE_24 = 24;
    private static final int FONT_SIZE_22 = 22;
    private Font FONT_SONG_BOLD = new Font("宋体", Font.BOLD, FONT_SIZE_24);
    private Font FONT_SONG_PLAIN = new Font("宋体", Font.PLAIN, FONT_SIZE_22);
    private static final Color WHITE = Color.white;
    private static final Color BLACK = Color.black;
    private static final Color FEMALE_COLOR = Color.magenta;
    private static final Color LIGHT_GRAY = Color.LIGHT_GRAY;

    public void createTechAuth(ImgUtil.CardImg cardImg) throws IOException {
        //画布整体布局背景为白色
        image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g_image = image.createGraphics();
        fillRect(g_image, WHITE, 0, 0, imageWidth, imageHeight);

        //插入陪玩师主图
        int H_mainPic = 700;
        int mainPic2Top = 0;
        drawImage(cardImg.getMainPicUrl(), 0, mainPic2Top, imageWidth, H_mainPic);

        //陪玩师个人信息区域
        int H_person = 70;
        int person2Top = mainPic2Top + H_mainPic + 10;
        String nickname = cardImg.getNickname();
        String genderAndAge = getGenderAndAge(cardImg.getGender(), cardImg.getAge());

        Graphics2D g_nickname = image.createGraphics();
        drawString(g_nickname, BLACK, FONT_SONG_BOLD, nickname, 5, person2Top + H_person / 2);
        int nicknameLen = getContentLength(nickname, g_nickname);
        int ageLen = getContentLength(genderAndAge, g_nickname);
        fillRect(g_nickname, FEMALE_COLOR, nicknameLen + 10, person2Top + 5, ageLen, H_person / 2);
        drawString(g_nickname, WHITE, FONT_SONG_PLAIN, genderAndAge, nicknameLen + 10, person2Top + H_person / 2);

        //技能和技能标签区域
        int H_tech = 70;
        int tech2Top = person2Top + H_person + 5;
        String techStr = cardImg.getTechStr();
        Graphics2D tech = image.createGraphics();

        //文字叠加,自动换行叠加
        int tempX = 0;
        int tempY = tech2Top;
        int tempCharLen = 0;//单字符长度
        int tempLineLen = 0;//单行字符总长度临时计算
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < techStr.length(); i++) {
            char tempChar = techStr.charAt(i);
            tempCharLen = getCharLen(tempChar, tech);
            tempLineLen += tempCharLen;
            if (tempLineLen >= imageWidth) {
                //长度已经满一行,进行文字叠加
                drawString(tech, BLACK, FONT_SONG_PLAIN, sb.toString(), tempX, tempY);
                sb.delete(0, sb.length());//清空内容,重新追加
                tempY += FONT_SONG_PLAIN.getSize() + 10;
                tempLineLen = 0;
            }
            sb.append(tempChar);//追加字符
        }
        //最后叠加余下的文字
        drawString(tech, BLACK, FONT_SONG_PLAIN, sb.toString(), tempX, tempY);
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
        String shareTitle = nickname + cardImg.getShareTitle();
        String shareContent = cardImg.getShareContent();

        drawImage(cardImg.getCodeUrl(), 10, code2Top, codeWidth, H_code);
        Graphics2D g_share = image.createGraphics();
        drawString(g_share, BLACK, FONT_SONG_BOLD, shareTitle, codeWidth + 30, code2Top + (H_code / 2) - 15);
        drawString(g_share, BLACK, FONT_SONG_PLAIN, shareContent, codeWidth + 30, code2Top + (H_code / 2) + 15);

        createImage("E:\\test\\aaaa.jpg");
    }

    //陪玩师技能名片分享
    public void createTechCard(ImgUtil.CardImg cardImg) throws IOException {
        //画布整体布局背景为白色
        image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g_image = image.createGraphics();
        fillRect(g_image, WHITE, 0, 0, imageWidth, imageHeight);

        //插入陪玩师主图
        int H_mainPic = 700;
        int mainPic2Top = 0;
        String mainPicUrl = cardImg.getMainPicUrl();
        drawImage(mainPicUrl, 0, mainPic2Top, imageWidth, H_mainPic);

        //陪玩师个人信息区域
        int H_person = 70;
        int person2Top = mainPic2Top + H_mainPic + 10;
        String nickname = cardImg.getNickname();
        String presonInfo = getGenderAndAge(cardImg.getGender(), cardImg.getAge());
        presonInfo += " " + cardImg.getCity();
        presonInfo += " " + cardImg.getPersonTagStr();
        Graphics2D g_nickname = image.createGraphics();
        drawString(g_nickname, BLACK, FONT_SONG_BOLD, nickname, 0, person2Top + H_person / 2);
        int personLen = getContentLength(nickname, g_nickname);
        int ageLen = getContentLength(presonInfo, g_nickname);
        fillRect(g_nickname, FEMALE_COLOR, personLen + 10, person2Top + 5, ageLen, H_person / 2);
        drawString(g_nickname, WHITE, FONT_SONG_PLAIN, presonInfo, personLen + 10, person2Top + H_person / 2);

        //技能和技能标签区域
        int H_tech = 70;
        int tech2Top = person2Top + H_person + 5;
        int icon_width = imageWidth / 6;
        Map<String, String> mainTechMap = cardImg.getMainTech();
        String iconUrl = mainTechMap.get("techIconUrl");

        drawImage(iconUrl, 10, tech2Top, icon_width, H_tech);
        String techAndTag = mainTechMap.get("techName");
        techAndTag += " " + mainTechMap.get("techTagStr");
        techAndTag += " " + mainTechMap.get("price");
        Graphics2D g_tech = image.createGraphics();
        drawString(g_tech, BLACK, FONT_SONG_PLAIN, techAndTag, icon_width + 10, tech2Top + H_tech / 2);

        //画一条横线
        Graphics2D line1 = image.createGraphics();
        int lineWidth = imageWidth - 20;
        int line2Top1 = tech2Top + H_tech;
        line1.setColor(LIGHT_GRAY);
        line1.drawRect(10, line2Top1, lineWidth, 0);

        Map<String, String> secTechMap = cardImg.getSecTech();
        int line2Top = line2Top1 + 10;
        if (!secTechMap.isEmpty()) {
            //其他技能
            String secIconUrl = secTechMap.get("techIconUrl");
            drawImage(secIconUrl, 10, line2Top1 + 10, icon_width, H_tech);
            String otherTechStr = secTechMap.get("techName");
            otherTechStr += " " + secTechMap.get("techTagStr");
            otherTechStr += " " + secTechMap.get("price");
            Graphics2D g_tech2 = image.createGraphics();
            drawString(g_tech2, BLACK, FONT_SONG_PLAIN, otherTechStr, icon_width + 10, line2Top1 + H_tech / 2 + 10);

            //画一条横线
            Graphics2D line = image.createGraphics();
            line2Top += H_tech;
            line.setColor(LIGHT_GRAY);
            line.drawRect(10, line2Top, lineWidth, 0);
        }
        //小程序码和文案
        int H_code = 200;
        int code2Top = line2Top + 10;
        int codeWidth = imageWidth / 3;
        String shareStr = cardImg.getShareStr();
        String codeUrl = cardImg.getCodeUrl();
        drawImage(codeUrl, 10, code2Top, codeWidth, H_code);

        Graphics2D g_share = image.createGraphics();
        drawString(g_share, BLACK, FONT_SONG_BOLD, shareStr, codeWidth + 30, code2Top + (H_code / 2) - 15);

        createImage("E:\\test\\bbbb.jpg");
    }

    private String getGenderAndAge(Integer gender, Integer age) {
        if (GenderEnum.ASEXUALITY.getType() == gender || GenderEnum.MALE.getType() == gender) {
            return GenderEnum.SYMBOL_MALE.getMsg() + " " + age;
        } else {
            return GenderEnum.SYMBOL_LADY.getMsg() + " " + age;
        }
    }

    private void fillRect(Graphics2D g, Color color, int x, int y, int width, int height) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    private void drawString(Graphics2D g, Color color, Font font, String str, int x, int y) {
        g.setColor(color);
        g.setFont(font);
        g.drawString(str, x, y);
    }

    private void drawImage(String imgUrl, int x, int y, int width, int height) throws IOException {
        BufferedImage bi = javax.imageio.ImageIO.read(new URL(imgUrl));
        if (bi != null) {
            Graphics g = image.getGraphics();
            g.drawImage(bi, x, y, width, height, null);
            g.dispose();
        }
    }

    private int getCharLen(char c, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charWidth(c);
    }

    private int getContentLength(String content, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charsWidth(content.toCharArray(), 0, content.length());
    }

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

    @Data
    @Builder
    public static class CardImg {
        private String nickname;
        private Integer gender;
        private Integer age;
        private String city;
        private String codeUrl;
        private String mainPicUrl;
        private String personTagStr;
        private String techStr;
        //技能分享的文案
        private String shareStr;
        //技能认证的文案
        private String shareTitle;
        private String shareContent;
        //主技能/主商品
        private Map<String, String> mainTech;
        //第二商品
        private Map<String, String> secTech;
    }
}
