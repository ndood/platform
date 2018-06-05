package com.fulu.game.common.utils;

import org.apache.commons.lang3.StringUtils;

public class EmojiTools {

    public static boolean containsEmoji(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }

        for (int i = 0; i < str.length(); i++) {


            int cp = str.codePointAt(i);


            if (isEmojiCharacter(cp)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEmojiCharacter(int first) {
        return !((first == 0x0) ||
                (first == 0x9) ||
                (first == 0xA) ||
                (first == 0xD) ||
                ((first >= 0x20) && (first <= 0xD7FF)) ||
                ((first >= 0xE000) && (first <= 0xFFFD)) ||
                ((first >= 0x10000))) ||
                (first == 0xa9 || first == 0xae || first == 0x2122 ||
                        first == 0x3030 || (first >= 0x25b6 && first <= 0x27bf) ||
                        first == 0x2328 || (first >= 0x23e9 && first <= 0x23fa))
                || ((first >= 0x1F000 && first <= 0x1FFFF))
                || ((first >= 0x2702) && (first <= 0x27B0))
                || ((first >= 0x1F601) && (first <= 0x1F64F));
    }

    public static String filterEmoji(String str) {

        if (!containsEmoji(str)) {
            return str;
        }
        StringBuilder buf = null;
        int len = str.length();
        for (int i = 0; i < len; i++) {
            char codePoint = str.charAt(i);
            if (!isEmojiCharacter(codePoint)) {
                if (buf == null) {
                    buf = new StringBuilder(str.length());
                }
                buf.append(codePoint);
            }
        }
        if (buf == null) {
            return "";
        } else {
            return buf.toString();
        }

    }
}
