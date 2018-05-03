package com.fulu.game.common.properties;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component(value = "configProperties")
@Accessors(chain = true)
@ConfigurationProperties(prefix = "config")
@Data
@Slf4j
@Validated
public class Config {

    private Evn evn = new Evn();

    private Oss oss = new Oss();

    private Cloopen cloopen = new Cloopen();

    private Wechat wechat = new Wechat();

    @Data
    public static class Evn{
        private String prefix;
    }

    @Data
    public static class Oss{
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;
        private String host;
    }

    @Data
    public static class Cloopen{
        private String serverIp;
        private String serverPort;
        private String accountSid;
        private String accountToken;
        private String appId;
    }


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
        private String tradeType;
        private String notifyUrl;

    }
}
