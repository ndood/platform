package com.fulu.game.app.service.impl;

import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.fulu.game.common.enums.PayBusinessEnum;
import com.fulu.game.common.enums.PaymentEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.PayRequestVO;
import com.fulu.game.core.service.impl.pay.OrderPayServiceImpl;
import com.fulu.game.core.service.impl.payment.AlipayPayment;
import com.fulu.game.core.service.impl.payment.BalancePayment;
import com.fulu.game.core.service.impl.payment.WeChatPayPayment;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AppOrderPayServiceImpl extends OrderPayServiceImpl {

    @Autowired
    private AppOrderServiceImpl appOrderService;
    @Autowired
    private BalancePayment balancePayment;
    @Autowired
    private AlipayPayment alipayPayment;
    @Autowired
    private WeChatPayPayment weChatPayPayment;

    @Override
    protected void payOrder(String orderNo, BigDecimal actualMoney) {
        appOrderService.payOrder(orderNo, actualMoney);
    }


    @Override
    protected PayRequestVO pay(Order order, User user, String ip) {
        PaymentEnum payment = PaymentEnum.getEnumByType(order.getPayment());
        PayRequestVO payRequestVO = new PayRequestVO();
        payRequestVO.setPayment(order.getPayment());
        //余额支付需要不需要调用支付请求
        if (PaymentEnum.BALANCE_PAY.equals(payment)) {
            boolean flag = balancePayment.balancePayOrder(user.getId(),order.getActualMoney(),order.getOrderNo());
            if (flag) {
                payOrder(order.getOrderNo(), order.getActualMoney());
            }
            payRequestVO.setPayArguments(true);
        } else if (PaymentEnum.ALIPAY_PAY.equals(payment)){
            AlipayTradeAppPayModel alipayTradeAppPayModel = alipayPayment.buildAlipayRequest(order);
            String payResponse = alipayPayment.payRequest(PayBusinessEnum.ORDER,alipayTradeAppPayModel);
            payRequestVO.setPayArguments(payResponse);
        } else if(PaymentEnum.WECHAT_PAY.equals(payment)){
            WxPayUnifiedOrderRequest wxPayUnifiedOrderRequest = weChatPayPayment.buildWxPayRequest(order,user,ip);
            Object payArguments = weChatPayPayment.payRequest(PayBusinessEnum.ORDER,WeChatPayPayment.WechatType.APP,wxPayUnifiedOrderRequest);
            payRequestVO.setPayArguments(payArguments);
        }
        return payRequestVO;
    }


    @Override
    protected Object parseResult(String xmlResult) throws WxPayException {




        return null;
    }

    @Override
    protected String getOrderNo(Object result) {
        return null;
    }

    @Override
    protected String getTotal(Object result) {
        return null;
    }

    @Override
    protected boolean thirdRefund(String orderNo, Integer totalMoney, Integer refundMoney) throws WxPayException {
        return false;
    }
}
