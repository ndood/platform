package com.fulu.game.core.service;


import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.payment.model.PayCallbackModel;
import com.fulu.game.core.entity.payment.model.PayRequestModel;
import com.fulu.game.core.entity.payment.model.RefundModel;
import com.fulu.game.core.entity.payment.res.PayRequestRes;
import com.github.binarywang.wxpay.exception.WxPayException;

import java.math.BigDecimal;

public interface OrderPayService {


    /**
     * 发起支付请求
     * @param payRequestModel
     * @return
     */
    PayRequestRes payRequest(PayRequestModel payRequestModel);


    /**
     * 支付回调
     * @param payCallbackModel
     * @return
     */
    boolean payResult(PayCallbackModel payCallbackModel);








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
    Boolean refund(String orderNo, BigDecimal totalMoney, BigDecimal RefundMoney) throws WxPayException;

    /**
     * 退全款
     *
     * @param orderNo
     * @param totalMoney
     * @return
     * @throws WxPayException
     */
    Boolean refund(String orderNo, BigDecimal totalMoney) throws WxPayException;
}
