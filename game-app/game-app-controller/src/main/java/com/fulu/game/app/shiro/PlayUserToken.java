package com.fulu.game.app.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 令牌
 */
public class PlayUserToken extends UsernamePasswordToken {

    private static final long serialVersionUID = 1L;
    private String mobile;
    private String authCode;

    public PlayUserToken(String mobile, String authCode) {
        super(mobile, "");
        this.mobile = mobile;
        this.authCode = authCode;
    }


    public String getMobile() {
        return mobile;
    }

    public String getAuthCode() {
        return authCode;
    }


}
