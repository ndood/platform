package com.fulu.game.core.service.impl.pay;

import com.fulu.game.common.exception.OrderException;
import com.fulu.game.core.entity.VirtualPayOrder;
import com.fulu.game.core.entity.payment.model.PayCallbackModel;
import com.fulu.game.core.entity.payment.model.PayRequestModel;
import com.fulu.game.core.entity.payment.model.RefundModel;
import com.fulu.game.core.entity.payment.res.PayCallbackRes;
import com.fulu.game.core.entity.payment.res.PayRequestRes;
import com.fulu.game.core.service.BusinessPayService;
import com.fulu.game.core.service.impl.payment.PaymentComponent;
import com.fulu.game.core.service.impl.payment.PaymentFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public abstract class VirtualOrderPayServiceImpl implements BusinessPayService {

    @Autowired
    private PaymentFactory paymentFactory;

    @Override
    public PayRequestRes payRequest(PayRequestModel payRequestModel) {
        VirtualPayOrder order = payRequestModel.getVirtualPayOrder();
        if (order.getIsPayCallback()) {
            throw new OrderException(order.getOrderNo(), "已支付的订单不能支付!");
        }
        //如果订单金额为0,则直接调用支付成功接口
        if (order.getActualMoney().compareTo(new BigDecimal(0))==0) {
            payOrder(order.getOrderNo(), order.getActualMoney());
            return new PayRequestRes(true);
        }
        try {
            PaymentComponent paymentComponent = paymentFactory.build(order.getPayment());
            PayRequestRes res= paymentComponent.payRequest(payRequestModel);
            if(res.isDirectPay()){
                payOrder(order.getOrderNo(), order.getActualMoney());
            }
            return res;
        } catch (Exception e) {
            log.error("订单支付错误", e);
            throw new OrderException(order.getOrderNo(), "订单无法支付!");
        }
    }

    @Override
    public boolean payResult(PayCallbackModel payCallbackModel) {
        PaymentComponent paymentComponent = paymentFactory.build(payCallbackModel.getPayment());
        PayCallbackRes res =paymentComponent.payCallBack(payCallbackModel);
        if(res.isSuccess()){
            payOrder(res.getOrderNO(),new BigDecimal(res.getPayMoney()));
        }
        return res.isSuccess();
    }

    @Override
    public boolean refund(RefundModel refundModel) {
        PaymentComponent paymentComponent = paymentFactory.build(refundModel.getPayment());
        boolean refundResult = paymentComponent.refund(refundModel);
        return refundResult;
    }

    public abstract void payOrder( String orderNo, BigDecimal actualMoney);


}
