package com.fulu.game.h5.shiro;

import lombok.Getter;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 令牌
 */
@Getter
public class PlayUserToken extends UsernamePasswordToken {

    private static final long serialVersionUID = 1L;

    private String fqlOpenid; //分期乐openId
    private String accessToken; //分期乐accessToken

    private Platform platform; //平台

    private String mpOpenId;  //微信openId
    private String unionId; //微信unionId
    private String mobile;  //手机号


    private Integer sourceId; //来源id

    private PlayUserToken(Builder builder) {
        setHost(builder.host);
        fqlOpenid = builder.fqlOpenid;
        accessToken = builder.accessToken;
        platform = builder.platform;
        mpOpenId = builder.mpOpenId;
        unionId = builder.unionId;
        mobile = builder.mobile;
        sourceId = builder.sourceId;
    }

    public static Builder newBuilder(Platform platform) {
        return new Builder(platform);
    }


    public static enum Platform{
        FENQILE,
        MP
    }

    public static final class Builder {
        private String host;
        private String fqlOpenid;
        private String accessToken;
        private Platform platform;
        private String mpOpenId;
        private String unionId;
        private String mobile;
        private Integer sourceId;

        private Builder(Platform platform) {
            this.platform = platform;
        }

        public Builder host(String val) {
            host = val;
            return this;
        }

        public Builder fqlOpenid(String val) {
            fqlOpenid = val;
            return this;
        }

        public Builder accessToken(String val) {
            accessToken = val;
            return this;
        }

        public Builder platform(Platform val) {
            platform = val;
            return this;
        }

        public Builder mpOpenId(String val) {
            mpOpenId = val;
            return this;
        }

        public Builder unionId(String val) {
            unionId = val;
            return this;
        }

        public Builder mobile(String val) {
            mobile = val;
            return this;
        }

        public Builder sourceId(Integer val) {
            sourceId = val;
            return this;
        }

        public PlayUserToken build() {
            return new PlayUserToken(this);
        }
    }
}
