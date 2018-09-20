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
import com.fulu.game.core.entity.payment.model.PayCallbackModel;
import com.fulu.game.core.entity.payment.model.PayRequestModel;
import com.fulu.game.core.entity.payment.model.RefundModel;
import com.fulu.game.core.entity.payment.res.PayCallbackRes;
import com.fulu.game.core.entity.payment.res.PayRequestRes;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
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
public class WeChatPayPaymentComponent implements PaymentComponent{



    private final Config configProperties;


    @Autowired
    public WeChatPayPaymentComponent(Config configProperties) {
        this.configProperties = configProperties;
    }


    @Override
    public PayRequestRes payRequest(PayRequestModel paymentVO) {
        log.info("执行微信支付请求:{}",paymentVO);
        PayRequestRes payRequestRes = new PayRequestRes(false);
        WxPayUnifiedOrderRequest request = null;
        User user = paymentVO.getUser();
        if (paymentVO.getPayBusinessEnum().equals(PayBusinessEnum.VIRTUAL_PRODUCT)) {
            VirtualPayOrder order = paymentVO.getVirtualPayOrder();
            request =buildWxPayRequest(order,user);
            Object result = payRequest(paymentVO.getPayBusinessEnum(),order.getPayPath(),request);
            payRequestRes.setRequestParameter(result);
        } else {
            Order order = paymentVO.getOrder();
            request = buildWxPayRequest(order,user);
            Object result = payRequest(paymentVO.getPayBusinessEnum(),order.getPlatform(),request);
            payRequestRes.setRequestParameter(result);
        }
        return payRequestRes;
    }


    @Override
    public PayCallbackRes payCallBack(PayCallbackModel payCallbackVO) {
        log.info("执行微信支付回调方法:{}",payCallbackVO);
        WxPayService wxPayService = buildPayService(payCallbackVO.getPayBusinessEnum(),payCallbackVO.getPlatform());
        PayCallbackRes payCallbackTO = new PayCallbackRes();
        payCallbackTO.setSuccess(false);
         try {
             WxPayOrderNotifyResult result = wxPayService.parseOrderNotifyResult(payCallbackVO.getWechatXmlResult());
             payCallbackTO.setOrderNO(result.getOutTradeNo());
             payCallbackTO.setPayMoney(BaseWxPayResult.feeToYuan(result.getTotalFee()));
             payCallbackTO.setSuccess(true);
         }catch (Exception e){
             log.info("微信回调解析异常:", e);
         }
        return payCallbackTO;
    }



    @Override
    public boolean refund(RefundModel refundModel) {
        log.info("执行微信退款业务方法:{}",refundModel);
        if(new BigDecimal(0).compareTo(refundModel.getRefundMoney())<=0){
            return true;
        }
        WxPayService wxPayService = buildPayService(refundModel.getPayBusinessEnum(),refundModel.getPlatform());
        Integer totalMoneyInt = (refundModel.getTotalMoney().multiply(new BigDecimal(100))).intValue();
        Integer refundMoneyInt;
        if (refundModel.getRefundMoney() == null) {
            refundMoneyInt = totalMoneyInt;
        } else {
            refundMoneyInt = (refundModel.getRefundMoney().multiply(new BigDecimal(100))).intValue();
        }
        WxPayRefundRequest request = new WxPayRefundRequest();
        request.setOutTradeNo(refundModel.getOrderNo());
        request.setOutRefundNo(refundModel.getOrderNo() + "E");
        request.setTotalFee(totalMoneyInt);
        request.setRefundFee(refundMoneyInt);
        try {
            wxPayService.refund(request);
            return true;
        }catch (Exception e){
            log.info("微信退款异常:", e);
        }
        return false;
    }



    /**
     * 发起微信支付请求
     * @param platform
     * @param wxPayUnifiedOrderRequest
     * @return
     */
    public Object payRequest(PayBusinessEnum payBusinessEnum,Integer platform, WxPayUnifiedOrderRequest wxPayUnifiedOrderRequest) {
        WxPayService wxPayService = buildPayService(payBusinessEnum,platform);
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
     * @return
     */
    private WxPayUnifiedOrderRequest buildWxPayRequest(Order order, User user) {
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody(order.getName());
        orderRequest.setOutTradeNo(order.getOrderNo());
        orderRequest.setTotalFee((order.getActualMoney().multiply(new BigDecimal(100))).intValue());//元转成分
        orderRequest.setSpbillCreateIp(order.getOrderIp());
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


    private WxPayUnifiedOrderRequest buildWxPayRequest(VirtualPayOrder order, User user) {
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody(order.getName());
        orderRequest.setOutTradeNo(order.getOrderNo());
        orderRequest.setTotalFee((order.getActualMoney().multiply(new BigDecimal(100))).intValue());//元转成分
        orderRequest.setSpbillCreateIp(order.getOrderIp());
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
     * @return
     */
    private WxPayService buildPayService(PayBusinessEnum payBusinessEnum,Integer platform) {

        Config.Wechat wechat = null;

        switch (PlatformEcoEnum.getEnumByType(platform)){
            case MP:
                wechat = configProperties.getWechat_mp();
                break;
            case PLAY:
                wechat = configProperties.getWechat_game();
                break;
            case POINT:
                wechat = configProperties.getWechat_poit();
                break;
            default:
                wechat = configProperties.getWechat_app();
        }

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
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;

    }



}
