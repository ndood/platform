package com.fulu.game.common.utils;

import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.GenderEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

@Component
public class ImgUtil {

    @Autowired
    private OssUtil ossUtil;

    private BufferedImage image;
    private String EXT_NAME = "jpg";
    private int imageWidth = Constant.TECH_CARD_WIDTH;
    private int imageHeight = Constant.TECH_CARD_HEIGHT;
    private static final int FONT_SIZE_24 = 24;
    private static final int FONT_SIZE_22 = 22;
    private Font FONT_SONG_BOLD = new Font("宋体", Font.BOLD, FONT_SIZE_24);
    private Font FONT_SONG_PLAIN = new Font("宋体", Font.PLAIN, FONT_SIZE_22);
    private static final Color WHITE = Color.white;
    private static final Color BLACK = Color.black;
    private static final Color FEMALE_COLOR = Color.pink;
    private static final Color LIGHT_GRAY = Color.LIGHT_GRAY;

    public String createTechAuth(Map<String, String> contentMap) throws IOException {
        //画布整体布局背景为白色
        image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g_image = image.createGraphics();
        fillRect(g_image, WHITE, 0, 0, imageWidth, imageHeight);

        //插入陪玩师主图
        int H_mainPic = 700;
        int mainPic2Top = 0;
        drawImage(contentMap.get("mainPicUrl"), 0, mainPic2Top, imageWidth, H_mainPic);

        //陪玩师个人信息区域
        int H_person = 70;
        int person2Top = mainPic2Top + H_mainPic + 10;
        String personContent = contentMap.get("nickname");
        String genderAndAge = getGenderAndAge(contentMap.get("gender"), contentMap.get("age"));

        Graphics2D g_person = image.createGraphics();
        drawString(g_person, BLACK, FONT_SONG_BOLD, personContent, 5, person2Top + H_person / 2);
        int personLen = getContentLength(personContent, g_person);
        int ageLen = getContentLength(genderAndAge, g_person);
        fillRect(g_person, FEMALE_COLOR, personLen + 10, person2Top + 5, ageLen, H_person / 2);
        drawString(g_person, WHITE, FONT_SONG_PLAIN, genderAndAge, personLen + 10, person2Top + H_person / 2);

        //技能和技能标签区域
        int H_tech = 70;
        int tech2Top = person2Top + H_person + 5;
        String techAndTag = contentMap.get("techAndTag");
        Graphics2D tech = image.createGraphics();

        //文字叠加,自动换行叠加
        int tempX = 0;
        int tempY = tech2Top;
        int tempCharLen = 0;//单字符长度
        int tempLineLen = 0;//单行字符总长度临时计算
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < techAndTag.length(); i++) {
            char tempChar = techAndTag.charAt(i);
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
        String shareTitle = personContent + contentMap.get("title");
        String shareContent = contentMap.get("content");

        drawImage(contentMap.get("codeUrl"), 10, code2Top, codeWidth, H_code);
        Graphics2D g_share = image.createGraphics();
        drawString(g_share, BLACK, FONT_SONG_BOLD, shareTitle, codeWidth + 30, code2Top + (H_code / 2) - 15);
        drawString(g_share, BLACK, FONT_SONG_PLAIN, shareContent, codeWidth + 30, code2Top + (H_code / 2) + 15);

        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        ImageOutputStream imageOS = ImageIO.createImageOutputStream(byteArrayOS);
        ImageIO.write(image, EXT_NAME, imageOS);
        InputStream is = new ByteArrayInputStream(byteArrayOS.toByteArray());
        String imgName = GenIdUtil.GetImgNo() + "." + EXT_NAME;
        return ossUtil.uploadFile(is, imgName);
    }

    //陪玩师技能名片分享
    public String createTechCard(Map<String, String> contentMap) throws IOException {
        //画布整体布局背景为白色
        image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g_image = image.createGraphics();
        fillRect(g_image, WHITE, 0, 0, imageWidth, imageHeight);

        //插入陪玩师主图
        int H_mainPic = 700;
        int mainPic2Top = 0;
        String mainPicUrl = contentMap.get("mainPicUrl");
        drawImage(mainPicUrl, 0, mainPic2Top, imageWidth, H_mainPic);

        //陪玩师个人信息区域
        int H_person = 70;
        int person2Top = mainPic2Top + H_mainPic + 10;
        String nickname = contentMap.get("nickname");
        String presonInfo = getGenderAndAge(contentMap.get("gender"), contentMap.get("age"));
        presonInfo += " " + contentMap.get("city");
        presonInfo += " " + contentMap.get("tagStr");
        Graphics2D g_person = image.createGraphics();
        drawString(g_person, BLACK, FONT_SONG_BOLD, nickname, 0, person2Top + H_person / 2);
        int personLen = getContentLength(nickname, g_person);
        int ageLen = getContentLength(presonInfo, g_person);
        fillRect(g_person, FEMALE_COLOR, personLen + 10, person2Top + 5, ageLen, H_person / 2);
        drawString(g_person, WHITE, FONT_SONG_PLAIN, presonInfo, personLen + 10, person2Top + H_person / 2);

        //技能和技能标签区域
        int H_tech = 70;
        int tech2Top = person2Top + H_person + 5;
        int icon_width = imageWidth / 6;
        String iconUrl = contentMap.get("mainTechIconUrl");

        drawImage(iconUrl, 10, tech2Top, icon_width, H_tech);
        String techAndTag = contentMap.get("mainTechName");
        techAndTag += " "+contentMap.get("mainTechTagStr");
        techAndTag += " "+contentMap.get("mainPrice");
        Graphics2D g_tech = image.createGraphics();
        drawString(g_tech, BLACK, FONT_SONG_PLAIN, techAndTag, icon_width + 10, tech2Top + H_tech / 2);

        //画一条横线
        Graphics2D line1 = image.createGraphics();
        int lineWidth1 = imageWidth - 20;
        int line2Top1 = tech2Top + H_tech;
        line1.setColor(LIGHT_GRAY);
        line1.drawRect(10, line2Top1, lineWidth1, 0);

        //其他技能
        drawImage(iconUrl, 10, line2Top1 + 10, icon_width, H_tech);
        String otherTechStr = contentMap.get("seccondTech");
        otherTechStr += " "+contentMap.get("secondTechTagStr");
        otherTechStr += " "+contentMap.get("secondPrice");
        Graphics2D g_tech2 = image.createGraphics();
        drawString(g_tech2, BLACK, FONT_SONG_PLAIN, otherTechStr, icon_width + 10, line2Top1 + H_tech / 2);

        //画一条横线
        Graphics2D line = image.createGraphics();
        int lineWidth = imageWidth - 20;
        int line2Top = line2Top1 +  H_tech + 10;
        line.setColor(LIGHT_GRAY);
        line.drawRect(10, line2Top, lineWidth, 0);

        //小程序码和文案
        int H_code = 200;
        int code2Top = line2Top + 10;
        int codeWidth = imageWidth / 3;
        String shareStr = contentMap.get("shareStr");
        String codeUrl = contentMap.get("codeUrl");
        drawImage(codeUrl, 10, code2Top, codeWidth, H_code);

        Graphics2D g_share = image.createGraphics();
        drawString(g_share, BLACK, FONT_SONG_BOLD, shareStr, codeWidth + 30, code2Top + (H_code / 2) - 15);

        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        ImageOutputStream imageOS = ImageIO.createImageOutputStream(byteArrayOS);
        ImageIO.write(image, EXT_NAME, imageOS);
        InputStream is = new ByteArrayInputStream(byteArrayOS.toByteArray());
        String imgName = GenIdUtil.GetImgNo() + "." + EXT_NAME;
        return ossUtil.uploadFile(is, imgName);

    }

    private String getGenderAndAge(String gender, String age) {
        if (GenderEnum.ASEXUALITY.getType().toString().equals(gender) || GenderEnum.MALE.getType().toString().equals(gender)) {
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

}
