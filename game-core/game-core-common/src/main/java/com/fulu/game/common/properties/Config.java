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


    @Data
    public static class Evn{

        private String prefix;

    }
}
