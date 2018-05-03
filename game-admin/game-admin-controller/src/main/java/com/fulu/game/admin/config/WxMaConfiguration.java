package com.fulu.game.admin.config;

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
        config.setAppid(configProperties.getWechat().getAppId());
        config.setSecret(configProperties.getWechat().getSecret());
        config.setToken(configProperties.getWechat().getToken());
        config.setAesKey(configProperties.getWechat().getAesKey());
        config.setMsgDataFormat(configProperties.getWechat().getMsgDataFormat());
        return config;
    }


    @Bean
    @ConditionalOnMissingBean
    public WxPayConfig payConfig() {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(configProperties.getWechat().getAppId());
        payConfig.setMchId(configProperties.getWechat().getMchId());
        payConfig.setMchKey(configProperties.getWechat().getMchKey());
        payConfig.setSubAppId(StringUtils.trimToNull(configProperties.getWechat().getSubAppId()));
        payConfig.setSubMchId(StringUtils.trimToNull(configProperties.getWechat().getSubMchId()));
        payConfig.setKeyPath(configProperties.getWechat().getKeyPath());
        payConfig.setNotifyUrl(configProperties.getWechat().getNotifyUrl());
        payConfig.setTradeType(configProperties.getWechat().getTradeType());
        return payConfig;
    }

    @Bean
    //@ConditionalOnMissingBean
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