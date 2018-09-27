package com.fulu.game.h5.config;


import com.fulu.game.common.properties.Config;
import com.fulu.game.thirdparty.fenqile.entity.FenqileConfig;
import com.fulu.game.thirdparty.fenqile.service.impl.FenqileAuthServiceImpl;
import com.fulu.game.thirdparty.fenqile.service.impl.FenqileOrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(Config.class)
@Slf4j
public class FenqileConfiguration {

    @Autowired
    private Config configProperties;

    @Bean
    public FenqileConfig fenqileConfig() {
        Config.Fenqile fenqile = configProperties.getFenqile();
        FenqileConfig fenqileConfig = new FenqileConfig();
        fenqileConfig.setV(fenqile.getVersion());
        fenqileConfig.setClientId(fenqile.getClientId());
        fenqileConfig.setClientSecret(fenqile.getClientSecret());
        fenqileConfig.setSellerId(fenqile.getSellerId());
        fenqileConfig.setPartnerKey(fenqile.getPartnerKey());
        fenqileConfig.setPartnerId(fenqile.getPartnerId());
        fenqileConfig.setOrderNoticeUrl(fenqile.getOrderNoticeUrl());
        fenqileConfig.setOrderDetailsUrl(fenqile.getOrderDetailsUrl());
        return fenqileConfig;
    }

    @Bean
    public FenqileAuthServiceImpl fenqileAuthService(FenqileConfig fenqileConfig){
        FenqileAuthServiceImpl fenqileAuthService = new FenqileAuthServiceImpl();
        fenqileAuthService.setConfig(fenqileConfig);
        return fenqileAuthService;
    }



    @Bean
    public FenqileOrderServiceImpl fenqileOrderService(FenqileConfig fenqileConfig){
        FenqileOrderServiceImpl fenqileAuthService = new FenqileOrderServiceImpl();
        fenqileAuthService.setConfig(fenqileConfig);
        return fenqileAuthService;
    }




}
