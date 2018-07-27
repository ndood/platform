package com.fulu.game.common.config;


import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WxMaServiceSupply {

    @Autowired
    private WxMaConfig gameMaconfig;
    @Autowired
    private WxPayConfig gamePayConfig;
    @Autowired
    private WxMaConfig pointMaconfig;
    @Autowired
    private WxPayConfig pointPayConfig;

    /**
     * 开黑陪玩支付服务
     * @return
     */
    public WxPayService gameWxPayService() {
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(gamePayConfig);
        return wxPayService;
    }

    /**
     * 开黑陪玩微信sdk服务
     * @return
     */
    public WxMaService gameWxMaService() {
        WxMaService wxMaService = new WxMaServiceImpl();
        wxMaService.setWxMaConfig(gameMaconfig);
        return wxMaService;
    }

    /**
     * 上分支付服务
     * @return
     */
    public WxPayService pointWxPayService() {
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(pointPayConfig);
        return wxPayService;
    }

    /**
     * 上分微信sdk服务
     * @return
     */
    public WxMaService pointWxMaService() {
        WxMaService wxMaService = new WxMaServiceImpl();
        wxMaService.setWxMaConfig(pointMaconfig);
        return wxMaService;
    }


}
