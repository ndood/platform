package com.fulu.game.common.utils;

/**
 * 获取和设置当前登录用户对象数据
 */
public class SubjectUtil {

    private static final ThreadLocal<Object> tl = new ThreadLocal<>();

    public static Object getCurrentUser() {
        return tl.get();
    }

    public static void setCurrentUset(Object o) {
        tl.set(o);
    }

}
