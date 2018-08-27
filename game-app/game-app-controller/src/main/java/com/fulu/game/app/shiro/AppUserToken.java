package com.fulu.game.app.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 令牌
 */
public class AppUserToken extends UsernamePasswordToken {

    private static final long serialVersionUID = 1L;
    private String mobile;
    private String verifyCode ;

    public AppUserToken(String mobile, String verifyCode) {
        super(mobile, "");
        this.mobile = mobile;
        this.verifyCode = verifyCode;
    }


    public String getMobile() {
        return mobile;
    }

    public String getVerifyCode() {
        return verifyCode;
    }
}
