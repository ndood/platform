package com.fulu.game.common.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 公众号服务提供
 *
 * @author Gong ZeChun
 * @date 2018/9/3 18:43
 */
@Component
public class WxMpServiceSupply {
    @Autowired
    private WxMpConfigStorage wxMpConfigStorage;
    @Autowired
    private WxPayConfig mpPayConfig;


    /**
     * 公众号服务提供
     *
     * @return
     */
    public WxMpService getWxMpService() {
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage);
        return wxMpService;
    }

    /**
     * 公众号支付服务
     * @return
     */
    public WxPayService wxMpPayService() {
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(mpPayConfig);
        return wxPayService;
    }
}
