package com.fulu.game.point.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 令牌
 */
public class PlayUserToken extends UsernamePasswordToken {

    private static final long serialVersionUID = 1L;
    private String openId;
    private String sessionKey;
    private Integer sourceId;
    public PlayUserToken(String openId,String sessionKey,Integer sourceId) {
        super(openId, "");
        this.openId = openId;
        this.sessionKey = sessionKey;
        this.sourceId = sourceId;
    }


    public String getOpenId(){
        return openId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public Integer getSourceId() {
        return sourceId;
    }
}
