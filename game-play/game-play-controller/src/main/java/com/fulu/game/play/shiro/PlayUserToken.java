package com.fulu.game.play.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 令牌
 */
public class PlayUserToken extends UsernamePasswordToken {

    private static final long serialVersionUID = 1L;

    private String openId;

    private String sessionKey;

    public void setOpenId(String openId){
        this.openId = openId;
    }
    public String getOpenId(){
        return openId;
    }
    public void setSessionKey(String sessionKey){
        this.sessionKey = sessionKey;
    }
    public String getSessionKey(){
        return sessionKey;
    }
    public PlayUserToken(String openId, String sessionKey) {
        super(openId, sessionKey);
        this.openId = openId;
        this.sessionKey = sessionKey;
    }
}
