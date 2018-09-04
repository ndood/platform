package com.fulu.game.common.config;

import com.fulu.game.common.properties.Config;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 公众号配置
 *
 * @author Gong ZeChun
 * @date 2018/9/3 18:43
 */
@Configuration
@EnableConfigurationProperties(Config.class)
public class WxMpConfiguration {

    @Autowired
    private Config configProperties;

    @Bean(value = "mpConfig")
    @Qualifier(value = "mpConfig")
    public WxMpConfigStorage mpConfig() {
        WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
        config.setAppId(configProperties.getWechat_mp().getAppId());
        config.setSecret(configProperties.getWechat_mp().getSecret());
        config.setToken(configProperties.getWechat_mp().getToken());
        config.setAesKey(configProperties.getWechat_mp().getAesKey());
        return config;
    }
}
