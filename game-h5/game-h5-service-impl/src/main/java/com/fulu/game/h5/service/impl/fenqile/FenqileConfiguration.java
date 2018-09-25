package com.fulu.game.h5.service.impl.fenqile;


import com.fulu.game.common.properties.Config;
import com.fulu.game.thirdparty.fenqile.entity.FenqileConfig;
import com.fulu.game.thirdparty.fenqile.service.impl.FenqileAuthServiceImpl;
import com.fulu.game.thirdparty.fenqile.service.impl.FenqileOrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(Config.class)
public class FenqileConfiguration {

    @Autowired
    private Config configProperties;

    @Bean
    public FenqileConfig fenqileConfig() {
        Config.Fenqile fenqile = configProperties.getFenqile();
        FenqileConfig fenqileConfig = new FenqileConfig();
        fenqileConfig.setV(fenqile.getVersion());
        fenqileConfig.setClientId(fenqileConfig.getClientId());
        fenqileConfig.setClientSecret(fenqileConfig.getClientSecret());
        fenqileConfig.setSellerId(fenqileConfig.getSellerId());
        fenqileConfig.setPartnerKey(fenqileConfig.getPartnerKey());
        fenqileConfig.setPartnerId(fenqileConfig.getPartnerId());
        return fenqileConfig;
    }

    @Bean
    public FenqileAuthServiceImpl fenqileAuthService(FenqileConfig fenqileConfig){
        FenqileAuthServiceImpl fenqileAuthService = new FenqileAuthServiceImpl();
        fenqileAuthService.setConfig(fenqileConfig);
        return fenqileAuthService;
    }



    @Bean
    public FenqileOrderServiceImpl FenqileOrderService(FenqileConfig fenqileConfig){
        FenqileOrderServiceImpl fenqileAuthService = new FenqileOrderServiceImpl();
        fenqileAuthService.setConfig(fenqileConfig);
        return fenqileAuthService;
    }




}
