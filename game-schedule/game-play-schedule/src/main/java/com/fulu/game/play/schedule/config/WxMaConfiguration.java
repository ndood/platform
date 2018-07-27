package com.fulu.game.play.schedule.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import com.fulu.game.common.properties.Config;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(Config.class)
public class WxMaConfiguration {


    @Autowired
    private Config configProperties;

    @Bean
    @ConditionalOnMissingBean
    public WxMaConfig maConfig() {
        WxMaInMemoryConfig config = new WxMaInMemoryConfig();
        config.setAppid(configProperties.getWechat_game().getAppId());
        config.setSecret(configProperties.getWechat_game().getSecret());
        config.setToken(configProperties.getWechat_game().getToken());
        config.setAesKey(configProperties.getWechat_game().getAesKey());
        config.setMsgDataFormat(configProperties.getWechat_game().getMsgDataFormat());
        return config;
    }


    @Bean
    @ConditionalOnMissingBean
    public WxPayConfig payConfig() {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(configProperties.getWechat_game().getAppId());
        payConfig.setMchId(configProperties.getWechat_game().getMchId());
        payConfig.setMchKey(configProperties.getWechat_game().getMchKey());
        payConfig.setSubAppId(StringUtils.trimToNull(configProperties.getWechat_game().getSubAppId()));
        payConfig.setSubMchId(StringUtils.trimToNull(configProperties.getWechat_game().getSubMchId()));
        payConfig.setKeyPath(configProperties.getWechat_game().getKeyPath());
        payConfig.setNotifyUrl(configProperties.getWechat_game().getNotifyUrl());
        payConfig.setTradeType(configProperties.getWechat_game().getTradeType());
        return payConfig;
    }


    @Bean
    public WxPayService wxPayService(WxPayConfig payConfig) {
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;
    }


    @Bean
    @ConditionalOnMissingBean
    public WxMaService wxMaService(WxMaConfig maConfig) {
        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(maConfig);
        return service;
    }


}