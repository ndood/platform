package com.fulu.game.common.utils;

import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.GenderEnum;
import lombok.Builder;
import lombok.Data;
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
import java.net.URL;
import java.util.Map;

@Component
public class ImgUtil {

    @Autowired
    private OssUtil ossUtil;

    private BufferedImage image;
    private String EXT_NAME = "jpg";
    private int imageWidth = Constant.TECH_CARD_WIDTH;
    private int cardImageHeight = Constant.TECH_CARD_HEIGHT;
    private int authImageHeight = Constant.TECH_AUTH_HEIGHT;
    private static int FONT_SIZE_24 = 24;
    private static int FONT_SIZE_20 = 20;
    private static int FONT_SIZE_18 = 18;
    private Font FONT_SONG_BOLD = new Font("黑体", Font.PLAIN, FONT_SIZE_24);
    private Font FONT_SONG_PLAIN = new Font("黑体", Font.PLAIN, FONT_SIZE_20);
    private Font FONT_SONG_PLAIN_18 = new Font("黑体", Font.PLAIN, FONT_SIZE_18);
    private static Color FEMALE_COLOR = new Color(255, 156, 163);
    private static Color LIGHT_GRAY = Color.LIGHT_GRAY;
    private int x_gap = 20, y_gap = 10;
    private int lineWidth = imageWidth - 20;

    public String createTechAuth(CardImg cardImg) throws IOException {

        //整体布局
        int mainPic_top = y_gap;
        int H_mainPic = 600;

        int person_top = mainPic_top + H_mainPic + y_gap;
        int H_person = 50;

        int tech_top = person_top + H_person + y_gap;
        int H_tech = 40;

        //小程序码
        int H_bottom = 30;//底部留白高度
        int H_wxcode = 220;
        int wxcode_top = authImageHeight - H_bottom - H_wxcode;
        int wxcodeWidth = imageWidth / 3;

        //画布整体布局背景为白色
        image = new BufferedImage(imageWidth, authImageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g_image = image.createGraphics();
        fillRect(g_image, Color.white, 0, 0, imageWidth, authImageHeight);

        //画主图
        drawImage(cardImg.getMainPicUrl(), x_gap, mainPic_top, imageWidth - 2 * x_gap, H_mainPic);
        //画昵称
        Graphics2D g_nickname = image.createGraphics();
        String nickname = cardImg.getNickname();
        drawString(g_nickname, Color.black, FONT_SONG_BOLD, nickname, x_gap, person_top + H_person / 2);
        int nicknameLen = getContentLength(nickname, g_nickname);
        //画性别和年龄
        String genderAndAge = getGenderAndAge(cardImg.getGender(), cardImg.getAge());
        int ageLen = getContentLength(genderAndAge, g_nickname);
        fillRound(g_nickname, FEMALE_COLOR, x_gap + nicknameLen + 10, person_top + y_gap - 1, ageLen, H_person / 2 - 5);
        drawString(g_nickname, Color.white, FONT_SONG_PLAIN, genderAndAge, nicknameLen + 30, person_top + H_person / 2);

        //技能+技能标签
        String techStr = cardImg.getTechStr();
        Graphics2D tech = image.createGraphics();

        //自动换行
        int tempX = 20;
        int tempY = tech_top + H_tech / 2;
        int tempCharLen = 0;//单字符长度
        int tempLineLen = 0;//单行字符总长度临时计算
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < techStr.length(); i++) {
            char tempChar = techStr.charAt(i);
            tempCharLen = getCharLen(tempChar, tech);
            tempLineLen += tempCharLen;
            if (tempLineLen >= imageWidth) {
                //长度已经满一行,进行文字叠加
                drawString(tech, Color.gray, FONT_SONG_PLAIN_18, sb.toString(), tempX, tempY);
                sb.delete(0, sb.length());//清空内容,重新追加
                tempY += FONT_SONG_PLAIN_18.getSize() + 10;
                tempLineLen = 0;
            }
            sb.append(tempChar);//追加字符
        }
        //最后叠加余下的文字
        drawString(tech, Color.gray, FONT_SONG_PLAIN_18, sb.toString(), tempX, tempY);
        tech.dispose();

        //画一条横线
        Graphics2D line = image.createGraphics();
        int line_top = (wxcode_top + tech_top + H_tech) / 2;
        drawRect(line, LIGHT_GRAY, 10, line_top, lineWidth, 0);

        //小程序码和文案
        drawImage(cardImg.getCodeUrl(), x_gap, wxcode_top, wxcodeWidth, H_wxcode);
        Graphics2D g_share = image.createGraphics();
        String shareTitle = nickname + cardImg.getShareTitle();
        drawString(g_share, Color.black, FONT_SONG_BOLD, shareTitle, wxcodeWidth + 50, wxcode_top + H_wxcode / 2 - 20);
        String shareContent = cardImg.getShareContent();
        drawString(g_share, Color.gray, FONT_SONG_PLAIN_18, shareContent, wxcodeWidth + 50, wxcode_top + H_wxcode / 2 + 15);
        return ossUpload();
    }

    //技能名片分享
    public String createTechCard(CardImg cardImg) throws IOException {
        //整体尺寸布局
        //主图
        int mainPic_top = 10;//顶部留白高度
        int H_mainPic = 640;
        //昵称 + 性别+年龄 + 城市+个人标签
        int name_top = mainPic_top + H_mainPic + y_gap;
        int H_nickname = 40;
        //横线1
        int line0_top = name_top + H_nickname + 10;
        //主技能
        int tech_top = line0_top + y_gap;
        int H_tech = 80;
        int icon_width = imageWidth / 9;
        //横线2
        int line1_top = tech_top + H_tech;
        //第二技能
        int secTech_top = line1_top + y_gap;
        int H_sectech = 80;
        int secicon_width = imageWidth / 9;
        //横线3
        int line2_top = secTech_top + H_sectech;
        //小程序码
        int H_bottom = 20;//底部留白高度
        int H_wxcode = 220;
        int wxcodeWidth = imageWidth / 3;
        int wxcode_top = cardImageHeight - H_bottom - H_wxcode;

        //画布整体布局背景为白色
        image = new BufferedImage(imageWidth, cardImageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g_image = image.createGraphics();
        fillRect(g_image, Color.white, 0, 0, imageWidth, cardImageHeight);
        //画主图
        String mainPicUrl = cardImg.getMainPicUrl();
        drawImage(mainPicUrl, x_gap, mainPic_top, imageWidth - 2 * x_gap, H_mainPic);
        //画昵称
        int x_start = x_gap;
        String nickname = cardImg.getNickname();
        Graphics2D g_nickname = image.createGraphics();
        drawString(g_nickname, Color.black, FONT_SONG_BOLD, nickname, x_start, name_top + 2 * H_nickname / 3);
        int nameLen = getContentLength(nickname, g_nickname);
        x_start += nameLen + x_gap;
        //画性别+年龄
        String genderAndAge = getGenderAndAge(cardImg.getGender(), cardImg.getAge());
        int ageLen = drawStrWithBgcolor(g_nickname, genderAndAge, x_start, name_top + 8, H_nickname / 2 + 2, +name_top + 2 * H_nickname / 3);
        x_start += ageLen + x_gap;
        //画城市
        String city = cardImg.getCity();
        int cityLen = drawStrWithBgcolor(g_nickname, city, x_start, name_top + 8, H_nickname / 2 + 2, +name_top + 2 * H_nickname / 3);
        x_start += cityLen + x_gap;
        //画个人标签
        String personTags = cardImg.getPersonTagStr();
        drawString(g_nickname, Color.gray, FONT_SONG_PLAIN_18, personTags, x_start, name_top + 2 * H_nickname / 3);

        //画横线1
        Graphics2D line0 = image.createGraphics();
        drawRect(line0, LIGHT_GRAY, 10, line0_top, lineWidth, 0);
        //画主技能 图标+技能名+标签+价格
        Map<String, String> mainTechMap = cardImg.getMainTech();
        String iconUrl = mainTechMap.get("techIconUrl");
        drawCircleImg(iconUrl, x_gap, tech_top, icon_width);

        Graphics2D g_tech = image.createGraphics();
        String techName = mainTechMap.get("techName");
        drawString(g_tech, Color.black, FONT_SONG_PLAIN, techName, icon_width + 50, tech_top + H_tech / 3);
        String techTag = mainTechMap.get("techTagStr");
        drawString(g_tech, Color.gray, FONT_SONG_PLAIN_18, techTag, icon_width + 50, tech_top + 2 * H_tech / 3);
        String price = mainTechMap.get("price");
        drawString(g_tech, Color.red, FONT_SONG_PLAIN_18, price, 7 * imageWidth / 10, tech_top + H_tech / 2);

        //画横线2
        Graphics2D line1 = image.createGraphics();
        drawRect(line1, LIGHT_GRAY, 10, line1_top, lineWidth, 0);
        //画其他技能+标签
        Map<String, String> secTechMap = cardImg.getSecTech();
        if (!secTechMap.isEmpty()) {
            String secIconUrl = secTechMap.get("techIconUrl");
            drawCircleImg(secIconUrl, x_gap, secTech_top, secicon_width);
            Graphics2D g_tech2 = image.createGraphics();
            String secTechName = secTechMap.get("techName");
            drawString(g_tech2, Color.black, FONT_SONG_PLAIN, secTechName, secicon_width + 50, secTech_top + H_sectech / 3);
            String secTagStr = secTechMap.get("techTagStr");
            drawString(g_tech, Color.gray, FONT_SONG_PLAIN_18, secTagStr, secicon_width + 50, secTech_top + 2 * H_tech / 3);
            String secPrice = secTechMap.get("price");
            drawString(g_tech, Color.red, FONT_SONG_PLAIN_18, secPrice, 7 * imageWidth / 10, secTech_top + H_tech / 2);
            //画一条横线3
            Graphics2D line2 = image.createGraphics();
            drawRect(line2, LIGHT_GRAY, x_gap, line2_top, lineWidth, 0);
        }
        //画小程序码+文案
        String shareStr = cardImg.getShareStr();
        String codeUrl = cardImg.getCodeUrl();
        drawImage(codeUrl, 10, wxcode_top, wxcodeWidth, H_wxcode);
        Graphics2D g_share = image.createGraphics();
        drawString(g_share, Color.black, FONT_SONG_BOLD, shareStr, wxcodeWidth + 30, wxcode_top + (H_wxcode / 2) - 15);
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

    private int drawStrWithBgcolor(Graphics2D g, String str, int start, int fill_y, int fill_h, int draw_h) {
        int len = getContentLength(str, g);
        fillRound(g, FEMALE_COLOR, start, fill_y, len, fill_h);
        drawString(g, Color.white, FONT_SONG_PLAIN_18, str, start, draw_h);
        return len;
    }

    private void drawImage(String imgUrl, int x, int y, int width, int height) throws IOException {
        BufferedImage bi = javax.imageio.ImageIO.read(new URL(imgUrl));
        if (bi != null) {
            Graphics g = image.getGraphics();
            g.drawImage(bi.getScaledInstance(width, height, Image.SCALE_SMOOTH), x, y, null);
            g.dispose();
        }
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
        Color color = Color.gray;
        BufferedImage srcImg0 = ImageIO.read(new URL(imgUrl));
        // 透明底的图片
        BufferedImage formatImage = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
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
