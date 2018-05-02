package com.fulu.game.play.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 令牌
 */
public class PlayUserToken extends UsernamePasswordToken {

    private static final long serialVersionUID = 1L;
    private String openId;
    public void setOpenId(String openId){
        this.openId = openId;
    }
    public String getOpenId(){
        return openId;
    }
    public PlayUserToken(String openId, String sessionKey) {
        super(openId, sessionKey);
        this.openId = openId;
    }
}
