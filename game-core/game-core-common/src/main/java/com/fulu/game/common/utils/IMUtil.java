package com.fulu.game.common.utils;

import com.fulu.game.common.properties.Config;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IMUtil {

    @Autowired
    private Config configProperties;
    private String imToken;
    private final int PSW_LENGTH = 6;

    /**
     * 请求token的url拼接
     *
     * @return
     */
    public String getTokenUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(configProperties.getIm().getUrlPrefix())
                .append("/")
                .append(configProperties.getIm().getOrgName())
                .append("/")
                .append(configProperties.getIm().getAppName())
                .append("/")
                .append("token");
        return sb.toString();
    }

    /**
     * 请求user的url拼接
     *
     * @return
     */
    public String getUserUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(configProperties.getIm().getUrlPrefix())
                .append("/")
                .append(configProperties.getIm().getOrgName())
                .append("/")
                .append(configProperties.getIm().getAppName())
                .append("/")
                .append("users");
        return sb.toString();
    }

    public String getTokenBodyStr() {
        JsonObject jo = new JsonObject();
        jo.addProperty("grant_type", configProperties.getIm().getGrantType());
        jo.addProperty("client_id", configProperties.getIm().getClientId());
        jo.addProperty("client_secret", configProperties.getIm().getClientSecret());
        return jo.toString();
    }

    /**
     * 生成随机密码
     *
     * @return
     */
    public static String generatePsw() {
        String code = "";
        String model = "0123456789abcdefghijklmnopqrstuvwxyz";
        char[] m = model.toCharArray();
        for (int j = 0; j < 6; j++) {
            char c = m[(int) (Math.random() * 36)];
            // 保证六位随机数之间没有重复的
            if (code.contains(String.valueOf(c))) {
                j--;
                continue;
            }
            code = code + c;
        }
        return code;

    }

    public void setImToken(String token) {
        this.imToken = token;
    }

    public String getImToken() {
        return this.imToken;
    }
}
