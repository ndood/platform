package com.fulu.game.core.service;


import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.github.binarywang.wxpay.exception.WxPayException;

import java.math.BigDecimal;

public interface PayService {

    /**
     * 订单支付
     *
     * @return 返回支付状态
     */
    Object payOrder(Order order, User user, String requestIp);

    /**
     * 支付回调
     *
     * @param xmlResult 回调的结果字符串
     * @return
     */
    String payResult(String xmlResult);

    /**
     * 部分退款
     *
     * @param orderNo
     * @param totalMoney
     * @param RefundMoney
     * @return
     * @throws WxPayException
     */
    Boolean refund(String orderNo, BigDecimal totalMoney, BigDecimal RefundMoney);

    /**
     * 退全款
     *
     * @param orderNo
     * @param totalMoney
     * @return
     * @throws WxPayException
     */
}
