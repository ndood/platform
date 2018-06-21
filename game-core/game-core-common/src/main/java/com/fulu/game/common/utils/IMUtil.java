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

    /**
     * 请求token的url拼接
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

    public void setImToken(String token) {
        this.imToken = token;
    }

    public String getImToken() {
        return this.imToken;
    }
}
