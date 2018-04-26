package com.fulu.game.play.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

public class PlayUserToken extends UsernamePasswordToken {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Boolean wechat = false;

    private String validationCode;

    public String getValidationCode() {
        return validationCode;
    }

    public void setValidationCode(String validationCode) {
        this.validationCode = validationCode;
    }

    public Boolean getWechat() {
        return wechat;
    }

    public void setWechat(Boolean wechat) {
        this.wechat = wechat;
    }

    public PlayUserToken(String username, String password, boolean rememberMe,
                    String host, String validationCode) {
        super(username, password, rememberMe, host);
        this.validationCode = validationCode;
    }
}
