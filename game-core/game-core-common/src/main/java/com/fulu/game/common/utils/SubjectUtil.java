package com.fulu.game.common.utils;


import com.fulu.game.common.domain.ClientInfo;

/**
 * 获取和设置当前登录用户对象数据
 */
public class SubjectUtil {

    private static final ThreadLocal<Object> tl = new ThreadLocal<>();
    private static final ThreadLocal<String> token = new ThreadLocal<>();
    private static final ThreadLocal<ClientInfo> userClientInfo = new ThreadLocal<>();

    public static ClientInfo getUserClientInfo() {
        return userClientInfo.get();
    }

    public static void setUserClientInfo(ClientInfo clientInfo) {
        userClientInfo.set(clientInfo);
    }

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
