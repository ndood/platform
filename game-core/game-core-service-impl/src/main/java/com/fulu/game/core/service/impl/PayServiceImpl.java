package com.fulu.game.core.service.impl;

import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.PayService;
import com.fulu.game.core.service.UserService;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.xiaoleilu.hutool.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Slf4j
public class PayServiceImpl implements PayService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private WxPayService wxPayService;

    @Override
    public WxPayMpOrderResult wechatUnifyOrder(String orderNo, String requestIp) {
        Order order = orderService.findByOrderNo(orderNo);
        User user = userService.findById(order.getUserId());
        if (!order.getIsPay() && !order.getStatus().equals(OrderStatusEnum.NON_PAYMENT.getStatus())) {
            throw new OrderException(orderNo, "已支付的订单不能支付!");
        }
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody(order.getName());
        orderRequest.setOutTradeNo(order.getOrderNo());
        Integer totalFee = (order.getTotalMoney().multiply(new BigDecimal(100))).intValue();
        orderRequest.setTotalFee(totalFee);//元转成分
        orderRequest.setOpenid(user.getOpenId());
        orderRequest.setSpbillCreateIp(requestIp);
        orderRequest.setTimeStart(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        try {
            WxPayMpOrderResult result = wxPayService.createOrder(orderRequest);
            return result;
        } catch (Exception e) {
            log.error("订单支付错误", e);
            throw new OrderException(orderNo, "订单无法支付!");
        }
    }

    @Override
    public String payResult(String xmlResult) {
        try {
            WxPayOrderNotifyResult result = wxPayService.parseOrderNotifyResult(xmlResult);
            // 结果正确
            String orderNo = result.getOutTradeNo();
            String totalYuan = BaseWxPayResult.feeToYuan(result.getTotalFee());
            orderService.payOrder(orderNo, totalYuan);
            return WxPayNotifyResponse.success("处理成功!");
        } catch (Exception e) {
            log.error("微信回调结果异常,异常原因{}", e.getMessage());
            return WxPayNotifyResponse.fail(e.getMessage());
        }
    }

    @Override
    public Boolean refund(String orderNo,BigDecimal totalMoney,BigDecimal refundMoney) throws WxPayException {
        Integer totalFee = (totalMoney.multiply(new BigDecimal(100))).intValue();
        Integer refunFee = totalFee;
        if(refundMoney!=null){
            refunFee = (refundMoney.multiply(new BigDecimal(100))).intValue();
        }
        WxPayRefundRequest request = new WxPayRefundRequest();
        request.setOutTradeNo(orderNo);
        request.setOutRefundNo(orderNo+"E");
        request.setTotalFee(totalFee);
        request.setRefundFee(refunFee);
        wxPayService.refund(request);
        return true;
    }

    @Override
    public Boolean refund(String orderNo, BigDecimal totalMoney) throws WxPayException{
        return refund(orderNo,totalMoney,null);
    }

}
