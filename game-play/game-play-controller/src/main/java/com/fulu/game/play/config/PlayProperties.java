package com.fulu.game.play.config;


import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Accessors(chain = true)
@ConfigurationProperties(prefix = "play")
@Data
@Slf4j
@Validated
public class PlayProperties {

    private Wechat wechat = new Wechat();


    @Data
    public static class Wechat{
        private String appId;
        private String secret;
        private String token;
        private String aesKey;
        private String msgDataFormat;
        private String mchId;
        private String mchKey;
        private String subAppId;
        private String subMchId;
        private String keyPath;

    }
}
