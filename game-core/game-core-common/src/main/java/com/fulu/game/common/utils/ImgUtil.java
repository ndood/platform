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
    private int imageWidth = Constant.TECH_CARD_WIDTH;  //图片的宽度400
    private int imageHeight = Constant.TECH_CARD_HEIGHT; //图片的高度600
    private static final int FONT_SIZE_16 = 16;
    private static final int FONT_SIZE_14 = 14;
    private Font FONT_SONG_BOLD_16 = new Font("宋体", Font.BOLD, FONT_SIZE_16);
    private Font FONT_SONG_PLAIN_14 = new Font("宋体", Font.PLAIN, FONT_SIZE_14);
    private static final Color WHITE = Color.white;
    private static final Color BLACK = Color.black;
    private static final Color FEMALE_COLOR = Color.pink;
    private static final Color LIGHT_GRAY = Color.LIGHT_GRAY;

    public String create(Map<String, String> contentMap) throws IOException {
        //画布整体布局背景为白色
        image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D main = image.createGraphics();
        main.setColor(WHITE);
        main.fillRect(0, 0, imageWidth, imageHeight);

        //插入陪玩师主图
        int H_mainPic = 330;
        int mainPic2Top = 0;
        Graphics mainPic = image.getGraphics();
        BufferedImage bimg = null;
        try {
            bimg = javax.imageio.ImageIO.read(new URL(contentMap.get("mainPicUrl")));
        } catch (Exception e) {
        }
        if (bimg != null) {
            mainPic.drawImage(bimg, 0, mainPic2Top, imageWidth, H_mainPic, null);
            mainPic.dispose();
        }

        //陪玩师个人信息区域
        int H_person = 40;
        int person2Top = mainPic2Top + H_mainPic + 10;
        String personContent = contentMap.get("nickname");
        Integer gender = Integer.valueOf(contentMap.get("gender"));
        Integer age = Integer.valueOf(contentMap.get("age"));
        //String ageContent = "♀22";
        Color defaultColor = FEMALE_COLOR;
        String genderAndAge = null;
        if (0 == gender || 1 == gender) {
            genderAndAge = GenderEnum.SYMBOL_MALE.getMsg() + " " + age;
        } else {
            defaultColor = FEMALE_COLOR;
            genderAndAge = GenderEnum.SYMBOL_LADY.getMsg() + " " + age;
        }
        Graphics2D person = image.createGraphics();
        person.setColor(WHITE);
        person.fillRect(0, person2Top, imageWidth, H_person);

        person.setColor(BLACK);
        person.setFont(FONT_SONG_BOLD_16);
        person.drawString(personContent, 0, person2Top + H_person / 2);
        int personLen = getContentLength(personContent, person);
        int ageLen = getContentLength(genderAndAge, person);
        person.setFont(FONT_SONG_PLAIN_14);
        person.setColor(defaultColor);
        person.fillRect(personLen + 10, person2Top + 5, ageLen, H_person / 2);
        person.setColor(WHITE);
        person.drawString(genderAndAge, personLen + 10, person2Top + H_person / 2);

        //*******技能和技能标签区域
        int H_tech = 50;
        int tech2Top = person2Top + H_person + 5;
        String techAndTag = contentMap.get("techAndTag");
        Graphics2D tech = image.createGraphics();
        tech.setColor(WHITE);
        tech.fillRect(0, tech2Top, imageWidth, H_tech);
        //设置字体
        tech.setColor(BLACK);
        tech.setFont(FONT_SONG_PLAIN_14);

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
                tech.drawString(sb.toString(), tempX, tempY);
                sb.delete(0, sb.length());//清空内容,重新追加
                tempY += FONT_SONG_PLAIN_14.getSize() + 5;
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
        line.setColor(LIGHT_GRAY);
        line.drawRect(10, line2Top, lineWidth, 0);

        //小程序码和文案
        int H_code = 130;
        int code2Top = line2Top + 10;
        int codeWidth = imageWidth / 3;
        int shareWidth = imageWidth - codeWidth;
        String shareTitle = personContent + contentMap.get("title");
        String shareContent = contentMap.get("content");

        Graphics code = image.getGraphics();
        BufferedImage codeImg = null;
        try {
            codeImg = javax.imageio.ImageIO.read(new URL(contentMap.get("codeUrl")));
        } catch (Exception e) {
        }
        if (codeImg != null) {
            code.drawImage(codeImg, 10, code2Top, codeWidth, H_code, null);
            code.dispose();
        }

        Graphics2D share = image.createGraphics();
        share.setColor(WHITE);
        share.fillRect(codeWidth + 20, code2Top, shareWidth - 10, H_code);
        share.setColor(BLACK);
        share.setFont(FONT_SONG_BOLD_16);
        share.drawString(shareTitle, codeWidth + 20, code2Top + (H_code / 2) - 10);
        share.setFont(FONT_SONG_PLAIN_14);
        share.drawString(shareContent, codeWidth + 20, code2Top + (H_code / 2) + 10);

        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        ImageOutputStream imageOS = ImageIO.createImageOutputStream(byteArrayOS);
        ImageIO.write(image, EXT_NAME, imageOS);
        InputStream is = new ByteArrayInputStream(byteArrayOS.toByteArray());
        String imgName = GenIdUtil.GetImgNo() + "." + EXT_NAME;
        return ossUtil.uploadFile(is,imgName);
    }

    private int getCharLen(char c, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charWidth(c);
    }

    private int getContentLength(String content, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charsWidth(content.toCharArray(), 0, content.length());
    }

}
