package com.fulu.game.core.service.impl.payment;

import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.PayBusinessEnum;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.common.exception.PayException;
import com.fulu.game.common.properties.Config;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualPayOrder;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by burgl on 2018/9/17.
 */
@Service
@Slf4j
public class WeChatPayPayment {


    public enum WechatType {
        PLAY, POINT, MP, APP;
    }


    private final Config configProperties;


    @Autowired
    public WeChatPayPayment(Config configProperties) {
        this.configProperties = configProperties;
    }


    /**
     * 发起微信支付请求
     * @param wechatType
     * @param wxPayUnifiedOrderRequest
     * @return
     */
    public Object payRequest(PayBusinessEnum payBusinessEnum,WechatType wechatType,  WxPayUnifiedOrderRequest wxPayUnifiedOrderRequest) {
        WxPayService wxPayService = null;
        switch (wechatType){
            case MP:
                wxPayService = buildPayService(payBusinessEnum,configProperties.getWechat_mp());
                break;
            case APP:
                wxPayService = buildPayService(payBusinessEnum,configProperties.getWechat_app());
                break;
            case PLAY:
                wxPayService = buildPayService(payBusinessEnum,configProperties.getWechat_game());
                break;
            case POINT:
                wxPayService = buildPayService(payBusinessEnum,configProperties.getWechat_poit());
                break;
        }
        try {
            log.info("发起微信支付请求:{}",wxPayUnifiedOrderRequest);
           return wxPayService.createOrder(wxPayUnifiedOrderRequest);
        }catch (Exception e){
           throw new PayException(PayException.ExceptionCode.WECHAT_CREATE_PAY_FAIL);
        }
    }

    /**
     * 构造微信支付请求
     *
     * @param order
     * @param user
     * @param ip
     * @return
     */
    public WxPayUnifiedOrderRequest buildWxPayRequest(Order order, User user, String ip) {
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody(order.getName());
        orderRequest.setOutTradeNo(order.getOrderNo());
        orderRequest.setTotalFee((order.getActualMoney().multiply(new BigDecimal(100))).intValue());//元转成分
        orderRequest.setSpbillCreateIp(ip);
        orderRequest.setTimeStart(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        if (PlatformEcoEnum.PLAY.getType().equals(order.getPlatform())) {
            orderRequest.setOpenid(user.getOpenId());
        } else if (PlatformEcoEnum.POINT.getType().equals(order.getPlatform())) {
            orderRequest.setOpenid(user.getPointOpenId());
        } else if (PlatformEcoEnum.MP.getType().equals(order.getPlatform())) {
            orderRequest.setOpenid(user.getPublicOpenId());
        }
        return orderRequest;
    }


    public WxPayUnifiedOrderRequest buildWxPayRequest(VirtualPayOrder order, User user, String ip) {
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody(order.getName());
        orderRequest.setOutTradeNo(order.getOrderNo());
        orderRequest.setTotalFee((order.getActualMoney().multiply(new BigDecimal(100))).intValue());//元转成分
        orderRequest.setSpbillCreateIp(ip);
        orderRequest.setTimeStart(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        if (PlatformEcoEnum.PLAY.getType().equals(order.getPayPath())) {
            orderRequest.setOpenid(user.getOpenId());
        } else if (PlatformEcoEnum.POINT.getType().equals(order.getPayPath())) {
            orderRequest.setOpenid(user.getPointOpenId());
        } else if (PlatformEcoEnum.MP.getType().equals(order.getPayPath())) {
            orderRequest.setOpenid(user.getPublicOpenId());
        }
        return orderRequest;
    }

    /**
     * 构造微信支付服务
     *
     * @param wechat
     * @return
     */
    public WxPayService buildPayService(PayBusinessEnum payBusinessEnum,Config.Wechat wechat) {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(wechat.getAppId());
        payConfig.setMchId(wechat.getMchId());
        payConfig.setMchKey(wechat.getMchKey());
        payConfig.setSubAppId(StringUtils.trimToNull(wechat.getSubAppId()));
        payConfig.setSubMchId(StringUtils.trimToNull(wechat.getSubMchId()));
        payConfig.setKeyPath(wechat.getKeyPath());
        if(PayBusinessEnum.ORDER.equals(payBusinessEnum)){
            payConfig.setNotifyUrl(wechat.getNotifyUrl());
        }else if(PayBusinessEnum.VIRTUAL_PRODUCT.equals(payBusinessEnum)){
            payConfig.setNotifyUrl(wechat.getPayVirtualProductNotifyUrl());
        }
        payConfig.setTradeType(wechat.getTradeType());
        WxPayService wxPayService = new com.github.binarywang.wxpay.service.impl.WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;

    }



}
