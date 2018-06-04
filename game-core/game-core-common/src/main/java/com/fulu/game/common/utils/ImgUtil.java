package com.fulu.game.common.utils;

import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.GenderEnum;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

@Component
public class ImgUtil {

    @Autowired
    private OssUtil ossUtil;

    private BufferedImage image;
    private String EXT_NAME = "jpg";
    private String locateIconUrl = "http://t-admin.wzpeilian.com/icon/place.png";
    private int imageWidth = Constant.TECH_CARD_WIDTH;
    private int default_cardImageHeight = Constant.TECH_CARD_HEIGHT;
    private int default_authImageHeight = Constant.TECH_AUTH_HEIGHT;
    private static int FONT_SIZE_32 = 32;
    private static int FONT_SIZE_28 = 28;
    private static int FONT_SIZE_24 = 24;
    private static int FONT_SIZE_22 = 22;
    private Font FONT_SONG_BOLD = new Font("黑体", Font.PLAIN, FONT_SIZE_32);
    private Font FONT_SONG_PLAIN_28 = new Font("黑体", Font.PLAIN, FONT_SIZE_28);
    private Font FONT_SONG_PLAIN_24 = new Font("黑体", Font.PLAIN, FONT_SIZE_24);
    private Font FONT_SONG_PLAIN_22 = new Font("黑体", Font.PLAIN, FONT_SIZE_22);
    private static Color FEMALE_COLOR = new Color(255, 156, 163);
    private static Color LIGHT_GRAY = Color.LIGHT_GRAY;
    private int x_gap_0 = 30,x_gap = 60, tag_x_gap = 20, tag_padding_y = 8, tag_h = 40, y_gap_1 = 30;
    private int lineWidth = imageWidth - 2 * x_gap;

    public String createTechAuth(CardImg cardImg) throws IOException {

        //整体布局
        int mainPic_top = 30;
        int H_mainPic = imageWidth-2*x_gap_0;

        int nickname_top = mainPic_top + H_mainPic + 35;
        int H_person = 30;

        int tech_top = nickname_top + H_person + y_gap_1;
        int H_tech = 40;

        //小程序码
        int H_bottom = 66;//底部留白高度
        int wxcodeWidth = 180;
        int wxcode_top = default_authImageHeight - H_bottom - wxcodeWidth;

        //画布整体布局背景为白色
        image = new BufferedImage(imageWidth, default_authImageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g_image = image.createGraphics();
        fillRect(g_image, new Color(245, 245 ,245), 0, 0, imageWidth, default_authImageHeight);
        fillRect(g_image, Color.white, x_gap_0, y_gap_1, imageWidth-2*x_gap_0, default_authImageHeight-2*y_gap_1);

        //画主图
        drawCornerImg(cardImg.getMainPicUrl(), x_gap_0, mainPic_top, H_mainPic, H_mainPic);
        //画昵称
        Graphics2D g_nickname = image.createGraphics();
        String nickname = cardImg.getNickname();
        drawString(g_nickname, new Color(20, 25, 28), FONT_SONG_BOLD, nickname, x_gap, nickname_top + FONT_SIZE_32);
        int nicknameLen = getContentLength(nickname, g_nickname);
        //画性别和年龄
        String genderAndAge = getGenderAndAge(cardImg.getGender(), cardImg.getAge());
        drawStrWithBgcolor(g_nickname,FEMALE_COLOR,Color.white,genderAndAge,nicknameLen+3*x_gap_0,nickname_top,40,nickname_top+tag_padding_y+FONT_SIZE_24);
        //技能+技能标签
        String techStr = cardImg.getTechStr();
        Graphics2D tech = image.createGraphics();
        //自动换行
        int tempX = x_gap;
        int tempY = tech_top + FONT_SIZE_24;
        int tempCharLen = 0;//单字符长度
        int tempLineLen = 0;//单行字符总长度临时计算
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < techStr.length(); i++) {
            char tempChar = techStr.charAt(i);
            tempCharLen = getCharLen(tempChar, tech);
            tempLineLen += tempCharLen;
            if (tempLineLen >= lineWidth) {
                //长度已经满一行,进行文字叠加
                drawString(tech, new Color(170,170,170), FONT_SONG_PLAIN_24, sb.toString(), tempX, tempY);
                sb.delete(0, sb.length());//清空内容,重新追加
                tempY += FONT_SONG_PLAIN_24.getSize() + 10;
                tempLineLen = 0;
            }
            sb.append(tempChar);//追加字符
        }
        //最后叠加余下的文字
        drawString(tech, new Color(170,170,170), FONT_SONG_PLAIN_24, sb.toString(), tempX, tempY);
        tech.dispose();

        //画一条横线
        Graphics2D line = image.createGraphics();
        int line_top = (wxcode_top + tempY) / 2;
        drawRect(line, LIGHT_GRAY, x_gap, line_top, lineWidth, 0);

        //小程序码和文案
        drawImage(cardImg.getCodeUrl(), x_gap, wxcode_top, wxcodeWidth, wxcodeWidth);
        Graphics2D g_share = image.createGraphics();
        String shareTitle = nickname + cardImg.getShareTitle();
        drawString(g_share, new Color(20, 25, 28), FONT_SONG_BOLD, shareTitle, x_gap+wxcodeWidth + 39, wxcode_top + wxcodeWidth / 2);
        String shareContent = cardImg.getShareContent();
        drawString(g_share, new Color(170,170,170), FONT_SONG_PLAIN_24, shareContent, x_gap+wxcodeWidth + 39, wxcode_top + wxcodeWidth / 2 + 15+FONT_SIZE_24);
        return ossUpload();
    }

    //技能名片分享
    public String createTechCard(CardImg cardImg) throws IOException {
        int cardImageHeight = default_cardImageHeight;//1467
        Map<String, String> secTechMap = cardImg.getSecTech();
        if (secTechMap.isEmpty()) {
            cardImageHeight = default_authImageHeight;//1206
        }
        //整体尺寸布局
        //主图
        int mainPic_top = 30;//顶部留白高度
        int H_mainPic = imageWidth-2*x_gap_0;
        //昵称 + 性别+年龄 + 城市+个人标签
        int name_top = mainPic_top + H_mainPic + 35;
        int H_nickname = 30;
        //城市
        int city_top = name_top + tag_h + 29;
        int H_city = FONT_SIZE_22;
        //横线0
        int line0_top = city_top + H_city + 30;
        //主技能
        int icon_width = 96;
        int tech_top = line0_top;
        int H_tech = 158;
        //横线1
        int line1_top = line0_top + H_tech;
        //第二技能
        int secTech_top = line1_top;
        int H_sectech = 158;
        //横线2
        int line2_top = secTech_top + H_sectech;
        //小程序码
        int H_bottom = 66;//底部留白高度
        int wxcodeWidth = 180;
        int wxcode_top = cardImageHeight - H_bottom - wxcodeWidth;

        //画布整体布局背景为白色
        image = new BufferedImage(imageWidth, cardImageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g_image = image.createGraphics();
        fillRect(g_image, new Color(245,245,245),0, 0, imageWidth, cardImageHeight);
        fillRect(g_image, Color.white, x_gap_0, y_gap_1, imageWidth-2*x_gap_0, cardImageHeight-2*y_gap_1);
        //画主图
        String mainPicUrl = cardImg.getMainPicUrl();
        //drawImage(mainPicUrl, x_gap_0, mainPic_top, H_mainPic, H_mainPic);
        drawCornerImg(mainPicUrl, x_gap_0, mainPic_top, H_mainPic, H_mainPic);
        //画昵称
        int x_start = x_gap;
        String nickname = cardImg.getNickname();
        Graphics2D g_nickname = image.createGraphics();
        drawString(g_nickname, new Color(20, 25, 28), FONT_SONG_BOLD, nickname, x_start, name_top + H_nickname);
        int nameLen = getContentLength(nickname, g_nickname);
        x_start += nameLen + x_gap_0;
        //画性别+年龄
        String genderAndAge = getGenderAndAge(cardImg.getGender(), cardImg.getAge());
        Graphics2D g_age = image.createGraphics();
        g_age.setFont(FONT_SONG_PLAIN_22);
        int ageLen = drawStrWithBgcolor(g_age, FEMALE_COLOR, Color.white, genderAndAge, x_start, name_top, 40, name_top + tag_padding_y + FONT_SIZE_22);
        x_start += ageLen + tag_x_gap;
        //画个人标签
        String personTag1 = cardImg.getPersonTag1();
        Graphics2D g_tag = image.createGraphics();
        g_tag.setFont(FONT_SONG_PLAIN_22);
        if (!StringUtils.isEmpty(personTag1)) {
            int personTag1Len = drawStrWithBgcolor(g_tag, FEMALE_COLOR, Color.white, personTag1, x_start, name_top, 40, name_top + tag_padding_y + FONT_SIZE_22);
            String personTag2 = cardImg.getPersonTag2();
            if (!StringUtils.isEmpty(personTag2)) {
                x_start += personTag1Len + tag_x_gap;
                drawStrWithBgcolor(g_tag, FEMALE_COLOR, Color.white, personTag2, x_start, name_top, 40, name_top + tag_padding_y + FONT_SIZE_22);
            }
        }
        //画城市和位置图标
        String city = cardImg.getCity();
        drawImage(locateIconUrl, x_gap, city_top, H_city, H_city);
        Graphics2D g_city = image.createGraphics();
        drawString(g_city, new Color(170, 170, 170), FONT_SONG_PLAIN_22, city, x_gap + 27, city_top + H_city);
        //画横线0
        Graphics2D line0 = image.createGraphics();
        drawRect(line0, LIGHT_GRAY, x_gap, line0_top, lineWidth, 0);
        //画主技能 图标+技能名+标签+价格
        int x_start_tech = x_gap;
        int y_start_tech = tech_top + y_gap_1;
        Map<String, String> mainTechMap = cardImg.getMainTech();
        String iconUrl = mainTechMap.get("techIconUrl");
        drawCircleImg(iconUrl, x_start_tech, y_start_tech, icon_width);
        x_start_tech += icon_width + x_gap;
        Graphics2D g_tech = image.createGraphics();
        String techName = mainTechMap.get("techName");
        drawString(g_tech, new Color(0, 0, 0), FONT_SONG_PLAIN_28, techName, x_start_tech, y_start_tech + FONT_SIZE_28);
        y_start_tech += FONT_SIZE_28 + 20;
        String techTag1 = mainTechMap.get("techTag1");
        int techTag1Len = drawStrWithBgcolor(g_tech, new Color(246, 246, 246), new Color(153, 153, 153), techTag1, x_start_tech, y_start_tech, 40, y_start_tech + tag_padding_y + FONT_SIZE_22);
        x_start_tech += techTag1Len + tag_x_gap;
        String techTag2 = mainTechMap.get("techTag2");
        if (!StringUtils.isEmpty(techTag2)) {
            drawStrWithBgcolor(g_tech, new Color(246, 246, 246), new Color(153, 153, 153), techTag2, x_start_tech, y_start_tech, 40, y_start_tech + tag_padding_y + FONT_SIZE_22);
        }
        String price = mainTechMap.get("price");
        drawString(g_tech, new Color(232, 100, 95), FONT_SONG_BOLD, price, 7 * imageWidth / 10, y_start_tech);

        //画横线2
        Graphics2D line1 = image.createGraphics();
        drawRect(line1, LIGHT_GRAY, x_gap, line1_top, lineWidth, 0);
        //画其他技能+标签
        if (!secTechMap.isEmpty()) {
            int x_start_tech2 = x_gap;
            int y_start_tech2 = secTech_top + y_gap_1;
            String secIconUrl = secTechMap.get("techIconUrl");
            drawCircleImg(secIconUrl, x_start_tech2, y_start_tech2, icon_width);
            x_start_tech2 += icon_width + x_gap;
            Graphics2D g_tech2 = image.createGraphics();
            String secTechName = secTechMap.get("techName");
            drawString(g_tech2, new Color(0, 0, 0), FONT_SONG_PLAIN_28, secTechName, x_start_tech2, y_start_tech2 + FONT_SIZE_28);
            y_start_tech2 += FONT_SIZE_28 + 20;
            String tech2Tag1 = secTechMap.get("techTag1");
            int tech2Tag1Len = drawStrWithBgcolor(g_tech2, new Color(246, 246, 246), new Color(153, 153, 153), tech2Tag1, x_start_tech2, y_start_tech2, 40, y_start_tech2 + tag_padding_y + FONT_SIZE_22);
            x_start_tech2 += tech2Tag1Len + tag_x_gap;
            String tech2Tag2 = secTechMap.get("techTag2");
            if (!StringUtils.isEmpty(tech2Tag2)) {
                drawStrWithBgcolor(g_tech2, new Color(246, 246, 246), new Color(153, 153, 153), tech2Tag2, x_start_tech2, y_start_tech2, 40, y_start_tech2 + tag_padding_y + FONT_SIZE_22);
            }
            String price2 = secTechMap.get("price");
            drawString(g_tech2, new Color(232, 100, 95), FONT_SONG_BOLD, price2, 3 * imageWidth / 5-5, y_start_tech2);

            //画一条横线3
            Graphics2D line2 = image.createGraphics();
            drawRect(line2, LIGHT_GRAY, x_gap, line2_top, lineWidth, 0);
        }
        //画小程序码
        String codeUrl = cardImg.getCodeUrl();
        drawImage(codeUrl, x_gap, wxcode_top, wxcodeWidth, wxcodeWidth);
        //文案自动换行处理
        Graphics2D g_share = image.createGraphics();
        g_share.setFont(FONT_SONG_BOLD);
        String shareStr = cardImg.getShareStr();
        //自动换行
        int tempX = 26 + wxcodeWidth + 50;
        int limitWidth = imageWidth-3*x_gap_0-tempX;
        int tempY = wxcode_top + (wxcodeWidth / 2);
        int tempCharLen = 0;//单字符长度
        int tempLineLen = 0;//单行字符总长度临时计算
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < shareStr.length(); i++) {
            char tempChar = shareStr.charAt(i);
            tempCharLen = getCharLen(tempChar, g_share);
            tempLineLen += tempCharLen;
            if (tempLineLen >= limitWidth) {
                //长度已经满一行,进行文字叠加
                drawString(g_share, new Color(50, 47, 46), FONT_SONG_BOLD, sb.toString(), tempX, tempY);
                sb.delete(0, sb.length());//清空内容,重新追加
                tempY += FONT_SONG_BOLD.getSize() + 10;
                tempLineLen = 0;
            }
            sb.append(tempChar);//追加字符
        }
        //最后叠加余下的文字
        drawString(g_share, new Color(50, 47, 46), FONT_SONG_BOLD, sb.toString(), tempX, tempY);
        g_share.dispose();
        return ossUpload();
    }

    /**
     * 上传
     *
     * @return
     * @throws IOException
     */
    private String ossUpload() throws IOException {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        ImageOutputStream imageOS = ImageIO.createImageOutputStream(byteArrayOS);
        ImageIO.write(image, EXT_NAME, imageOS);
        InputStream is = new ByteArrayInputStream(byteArrayOS.toByteArray());
        String imgName = GenIdUtil.GetImgNo() + "." + EXT_NAME;
        return ossUtil.uploadFile(is, imgName);
    }

    private String getGenderAndAge(Integer gender, Integer age) {
        if (GenderEnum.ASEXUALITY.getType() == gender || GenderEnum.MALE.getType() == gender) {
            return GenderEnum.SYMBOL_MALE.getMsg() + age;
        } else {
            return GenderEnum.SYMBOL_LADY.getMsg() + age;
        }
    }

    private void drawRect(Graphics2D g, Color c, int x, int y, int width, int height) {
        g.setColor(c);
        g.drawRect(x, y, width, height);
    }

    /**
     * 画矩形并填充
     *
     * @param g
     * @param color
     * @param x
     * @param y
     * @param width
     * @param height
     */
    private void fillRect(Graphics2D g, Color color, int x, int y, int width, int height) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    /**
     * 画圆角矩形并填充
     *
     * @param g
     * @param color
     * @param x
     * @param y
     * @param width
     * @param height
     */
    private void fillRound(Graphics2D g, Color color, int x, int y, int width, int height) {
        g.setColor(color);
        RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width, height, 6, 6);
        g.draw(rect);
        g.fillRect(x, y, width, height);
    }

    /**
     * 画文字
     *
     * @param g
     * @param color
     * @param font
     * @param str
     * @param x
     * @param y
     */
    private void drawString(Graphics2D g, Color color, Font font, String str, int x, int y) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.setFont(font);
        g.drawString(str, x, y);
    }

    private int drawStrWithBgcolor(Graphics2D g, Color bgColor, Color wordColor, String str, int start, int fill_y, int fill_h, int draw_h) {
        int len = getContentLength(str, g);
        fillRound(g, bgColor, start, fill_y, len + 2 * 15, fill_h);
        drawString(g, wordColor, g.getFont(), str, start + 15, draw_h);
        return len + 2 * 15;
    }

    private void drawImage(String imgUrl, int x, int y, int width, int height) throws IOException {
        BufferedImage bi = javax.imageio.ImageIO.read(new URL(imgUrl));
        if (bi != null) {
            Graphics g = image.getGraphics();
            g.drawImage(bi.getScaledInstance(width, height, Image.SCALE_SMOOTH), x, y, null);
            g.dispose();
        }
    }

    private void drawCornerImg(String imgUrl, int x, int y, int width, int height) throws IOException {
        BufferedImage sourceImg = ImageIO.read(new URL(imgUrl));
        int w = sourceImg.getWidth();
        int h = sourceImg.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = output.createGraphics();
        output = g2.getDeviceConfiguration().createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        g2.dispose();
        g2 = output.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillRoundRect(0, 0,w, h, 40, 40);
        g2.setComposite(AlphaComposite.SrcIn);
        g2.drawImage(sourceImg, 0, 0, w, h, null);
        g2.dispose();
        Graphics2D g = image.createGraphics();
        g.drawImage(output,x,y,width,height,null);
    }

    /**
     * 图片裁剪成圆形画出来
     *
     * @param imgUrl 图片网络地址
     * @param x      左上角横坐标
     * @param y      左上角纵坐标
     * @param width  外切正方形边长
     * @throws IOException
     */
    private void drawCircleImg(String imgUrl, int x, int y, int width) throws IOException {
        //自定义线条宽度、圆圈距离正方形的边距，圆圈颜色
        float strokeWidth = 1.0F;
        int border = 1;
        Color color = Color.white;
        BufferedImage srcImg0 = ImageIO.read(new URL(imgUrl));
        // 透明底的图片
        BufferedImage formatImage = new BufferedImage(width, width, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Ellipse2D.Double shape = new Ellipse2D.Double(x, y, width, width);
        g.setClip(shape);
        g.drawImage(srcImg0, x, y, width, width, null);
        g.dispose();

        //新创建一个graphics，这样画的圆不会有锯齿
        g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //画笔用法:线条宽度+线段类型+连接处形状:https://blog.csdn.net/li_tengfei/article/details/6098093
        Stroke s = new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g.setStroke(s);
        //线的颜色
        g.setColor(color);
        g.drawOval(x + border, y + border, width - border * 2, width - border * 2);
        g.dispose();
    }

    private int getCharLen(char c, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charWidth(c);
    }

    private int getContentLength(String content, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charsWidth(content.toCharArray(), 0, content.length());
    }

    @Data
    @Builder
    public static class CardImg {
        private String nickname;
        private Integer gender;
        private Integer age;
        private String personTagStr;
        private String personTag1;
        private String personTag2;
        private String city;
        private String codeUrl;
        private String mainPicUrl;
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
