package com.fulu.game.core.service.impl.pay;

import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.core.entity.Order;
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
public abstract class OrderPayServiceImpl implements BusinessPayService {

    @Autowired
    private PaymentFactory paymentFactory;


    /**
     * 发起支付请求
     * @param payRequestModel
     * @return
     */
    @Override
    public PayRequestRes payRequest(PayRequestModel payRequestModel) {
        Order order = payRequestModel.getOrder();
        if (order.getIsPay() && !order.getStatus().equals(OrderStatusEnum.NON_PAYMENT.getStatus())) {
            throw new OrderException(order.getOrderNo(), "已支付的订单不能支付!");
        }
        Integer totalFee = (order.getActualMoney().multiply(new BigDecimal(100))).intValue();
        //如果订单金额为0,则直接调用支付成功接口
        if (totalFee.equals(0)) {
            payOrder(order.getPayment(),order.getOrderNo(), order.getActualMoney());
            return new PayRequestRes(true);
        }
        try {
            PaymentComponent paymentComponent = paymentFactory.build(order.getPayment());
            PayRequestRes res= paymentComponent.payRequest(payRequestModel);
            if(res.isDirectPay()){
                payOrder(order.getPayment(),order.getOrderNo(), order.getActualMoney());
            }
            return res;
        } catch (Exception e) {
            log.error("订单支付错误", e);
            throw new OrderException(order.getOrderNo(), "订单无法支付!");
        }
    }


    /**
     * 支付结果接口
     * @param payCallbackModel
     * @return
     */
    @Override
    public boolean payResult(PayCallbackModel payCallbackModel){
        PaymentComponent paymentComponent = paymentFactory.build(payCallbackModel.getPayment());
        PayCallbackRes res =paymentComponent.payCallBack(payCallbackModel);
        if(res.isSuccess()){
            payOrder(payCallbackModel.getPayment(),res.getOrderNO(),new BigDecimal(res.getPayMoney()));
        }
        return res.isSuccess();
    }

    /**
     * 退款
     * @param refundModel
     * @return
     */
    @Override
    public boolean refund(RefundModel refundModel){

        PaymentComponent paymentComponent = paymentFactory.build(refundModel.getPayment());
        boolean refundResult = paymentComponent.refund(refundModel);
        return refundResult;
    }


    /**
     * 修改订单的支付状态
     * @param orderNo
     * @param actualMoney
     */
    public abstract void payOrder(Integer payment,String orderNo, BigDecimal actualMoney);





}
