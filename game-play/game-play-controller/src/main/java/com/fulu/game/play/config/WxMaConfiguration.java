package com.fulu.game.play.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
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
@EnableConfigurationProperties(PlayProperties.class)
public class WxMaConfiguration {


    @Autowired
    private PlayProperties playProperties;

    @Bean
    @ConditionalOnMissingBean
    public WxMaConfig maConfig() {
        WxMaInMemoryConfig config = new WxMaInMemoryConfig();
        config.setAppid(playProperties.getWechat().getAppId());
        config.setSecret(playProperties.getWechat().getSecret());
        config.setToken(playProperties.getWechat().getToken());
        config.setAesKey(playProperties.getWechat().getAesKey());
        config.setMsgDataFormat(playProperties.getWechat().getMsgDataFormat());

        return config;
    }


    @Bean
    @ConditionalOnMissingBean
    public WxPayConfig payConfig() {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(playProperties.getWechat().getAppId());
        payConfig.setMchId(playProperties.getWechat().getMchId());
        payConfig.setMchKey(playProperties.getWechat().getMchKey());
        payConfig.setSubAppId(StringUtils.trimToNull(playProperties.getWechat().getSubAppId()));
        payConfig.setSubMchId(StringUtils.trimToNull(playProperties.getWechat().getSubMchId()));
        payConfig.setKeyPath(playProperties.getWechat().getKeyPath());
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