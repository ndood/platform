package com.fulu.game.common.utils;

/**
 * 获取和设置当前登录用户对象数据
 */
public class SubjectUtil {

    private static final ThreadLocal<Object> tl = new ThreadLocal<>();
    private static final ThreadLocal<String> token = new ThreadLocal<>();

    public static Object getCurrentUser() {
        return tl.get();
    }

    public static void setCurrentUser(Object o) {
        tl.set(o);
    }

    public static String getToken() {
        return token.get();
    }

    public static void setToken(String genToken) {
        token.set(genToken);
    }

}
