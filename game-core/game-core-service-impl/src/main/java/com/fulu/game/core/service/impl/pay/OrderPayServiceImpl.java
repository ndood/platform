package com.fulu.game.core.service.impl.pay;

import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.OrderPayService;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.exception.WxPayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public abstract class OrderPayServiceImpl<T> implements OrderPayService {

    @Override
    public Object payOrder(Order order, User user, String requestIp) {
        if (order.getIsPay() && !order.getStatus().equals(OrderStatusEnum.NON_PAYMENT.getStatus())) {
            throw new OrderException(order.getOrderNo(), "已支付的订单不能支付!");
        }
        Integer totalFee = (order.getActualMoney().multiply(new BigDecimal(100))).intValue();
        //如果订单金额为0,则直接调用支付成功接口
        if (totalFee.equals(0)) {
            payOrder(order.getOrderNo(), order.getActualMoney());
            return null;
        }
        try {
            log.info("订单支付:orderNo:{};order:{};requestIp:{};", order.getOrderNo(), order, requestIp);
            return pay(order, user, requestIp);
        } catch (Exception e) {
            log.error("订单支付错误", e);
            throw new OrderException(order.getOrderNo(), "订单无法支付!");
        }
    }

    /**
     * 修改订单的支付状态
     * @param orderNo
     * @param actualMoney
     */
    protected abstract void payOrder(String orderNo, BigDecimal actualMoney);


    protected abstract Object pay(Order order, User user, String ip);

    @Override
    public String payResult(String xmlResult) {
        try {
            T result = parseResult(xmlResult);
            // 结果正确
            String orderNo = getOrderNo(result);
            String totalYuan = getTotal(result);
            payOrder(orderNo, new BigDecimal(totalYuan));
            return WxPayNotifyResponse.success("处理成功!");
        } catch (Exception e) {
            log.error("回调报文:{}", xmlResult);
            log.error("回调结果异常,异常原因:", e);
            return WxPayNotifyResponse.fail(e.getMessage());
        }
    }

    protected abstract T parseResult(String xmlResult) throws WxPayException;

    protected abstract String getOrderNo(T result);

    protected abstract String getTotal(T result);

    @Override
    public Boolean refund(String orderNo, BigDecimal totalMoney) throws WxPayException {
        return refund(orderNo, totalMoney, null);
    }

    @Override
    public Boolean refund(String orderNo, BigDecimal totalMoney, BigDecimal refundMoney) throws WxPayException {
        Integer totalMoneyInt = (totalMoney.multiply(new BigDecimal(100))).intValue();
        Integer refundMoneyInt;
        if (refundMoney == null) {
            refundMoneyInt = totalMoneyInt;
        } else {
            refundMoneyInt = (refundMoney.multiply(new BigDecimal(100))).intValue();
        }

        log.info("退款:orderNo:{};refunFee:{};", orderNo, refundMoneyInt);
        return (refundMoneyInt.equals(0)) || thirdRefund(orderNo, totalMoneyInt, refundMoneyInt);
    }

    protected abstract boolean thirdRefund(String orderNo, Integer totalMoney, Integer refundMoney) throws WxPayException;




}
