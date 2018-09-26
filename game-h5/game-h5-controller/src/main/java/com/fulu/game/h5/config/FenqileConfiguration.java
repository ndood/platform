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
        log.info("执行分期乐修改订单回调通知接口");
        fenqileAuthService.noticeModify(1,fenqileConfig.getOrderNoticeUrl());
        return fenqileAuthService;
    }




}
