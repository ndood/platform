package com.fulu.game.h5.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 令牌
 */
public class PlayUserToken extends UsernamePasswordToken {

    private static final long serialVersionUID = 1L;

    private String fqlOpenid;
    private Integer sourceId;
    private String accessToken;

    private String mpOpenId;
    private String unionId;
    private String mobile;



    public PlayUserToken(String fqlOpenid) {
        super(fqlOpenid, "");
        this.fqlOpenid = fqlOpenid;
    }


    public PlayUserToken(String fqlOpenid, String accessToken) {
        super(fqlOpenid, "");
        this.fqlOpenid = fqlOpenid;
        this.accessToken = accessToken;
    }

    public PlayUserToken(String fqlOpenid, String accessToken, Integer sourceId) {
        super(fqlOpenid, "");
        this.fqlOpenid = fqlOpenid;
        this.accessToken = accessToken;
        this.sourceId = sourceId;
    }






    public String getFqlOpenid() {
        return fqlOpenid;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public String getAccessToken() {
        return accessToken;
    }

}
