package com.fulu.game.h5.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 令牌
 */
public class PlayUserToken extends UsernamePasswordToken {

    private static final long serialVersionUID = 1L;
    private String fqlOpenid;
    private Integer sourceId;
    public PlayUserToken(String fqlOpenid,Integer sourceId) {
        super(fqlOpenid, "");
        this.fqlOpenid = fqlOpenid;
        this.sourceId = sourceId;

    }

    public String getFqlOpenid() {
        return fqlOpenid;
    }

    public Integer getSourceId() {
        return sourceId;
    }
}
