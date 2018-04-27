package com.fulu.game.common.properties;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Accessors(chain = true)
@ConfigurationProperties(prefix = "config")
@Data
@Slf4j
@Validated
public class Config {

    private Evn evn = new Evn();

    private Oss oss = new Oss();

    private Cloopen cloopen = new Cloopen();

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
}
