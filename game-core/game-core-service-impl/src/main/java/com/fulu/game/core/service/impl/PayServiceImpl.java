package com.fulu.game.core.service.impl;

import com.fulu.game.common.config.WxMaServiceSupply;
import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.enums.OrderTypeEnum;
import com.fulu.game.common.enums.WechatEcoEnum;
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
    private WxMaServiceSupply wxMaServiceSupply;

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
        Integer totalFee = (order.getActualMoney().multiply(new BigDecimal(100))).intValue();
        //如果订单金额为0,则直接调用支付成功接口
        if(totalFee.equals(0)){
            orderService.payOrder(orderNo,order.getActualMoney());
            return null;
        }
        orderRequest.setTotalFee(totalFee);//元转成分
        orderRequest.setSpbillCreateIp(requestIp);
        orderRequest.setTimeStart(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        try {
            log.info("订单支付:orderNo:{};order:{};requestIp:{};",orderNo,order,requestIp);
            WxPayMpOrderResult result = null;
            //不同小程序订单调用不同的微信支付
            if(OrderTypeEnum.PLATFORM.getType().equals(order.getType())){
                orderRequest.setOpenid(user.getOpenId());
                result = wxMaServiceSupply.playWxPayService().createOrder(orderRequest);
            }else if(OrderTypeEnum.POINT.getType().equals(order.getType())){
                orderRequest.setOpenid(user.getPointOpenId());
                result = wxMaServiceSupply.pointWxPayService().createOrder(orderRequest);
            }
            return result;
        } catch (Exception e) {
            log.error("订单支付错误", e);
            throw new OrderException(orderNo, "订单无法支付!");
        }
    }


    @Override
    public String payResult(String xmlResult,WechatEcoEnum wechatEcoEnum) {
        try {
            WxPayOrderNotifyResult result = null;
            if(WechatEcoEnum.PLAY.equals(wechatEcoEnum)){
                result = wxMaServiceSupply.playWxPayService().parseOrderNotifyResult(xmlResult);
            }else{
                result = wxMaServiceSupply.pointWxPayService().parseOrderNotifyResult(xmlResult);
            }
            // 结果正确
            String orderNo = result.getOutTradeNo();
            String totalYuan = BaseWxPayResult.feeToYuan(result.getTotalFee());
            orderService.payOrder(orderNo, new BigDecimal(totalYuan));
            return WxPayNotifyResponse.success("处理成功!");
        } catch (Exception e) {
            log.error("回调报文:{}", xmlResult);
            log.error("微信回调结果异常,异常原因:", e);
            return WxPayNotifyResponse.fail(e.getMessage());
        }
    }


    @Override
    public Boolean refund(String orderNo, BigDecimal totalMoney, BigDecimal refundMoney) throws WxPayException {
        Order order = orderService.findByOrderNo(orderNo);
        Integer totalMoneyInt = (totalMoney.multiply(new BigDecimal(100))).intValue();
        Integer refundMoneyInt;
        if(refundMoney == null) {
            refundMoneyInt = totalMoneyInt;
        }else {
            refundMoneyInt = (refundMoney.multiply(new BigDecimal(100))).intValue();
        }

        log.info("退款:orderNo:{};refunFee:{};", orderNo, refundMoneyInt);
        if(refundMoneyInt.equals(0)){
            return true;
        }
        WxPayRefundResult wxPayRefundResult = null;
        WxPayRefundRequest request = new WxPayRefundRequest();
        request.setOutTradeNo(orderNo);
        request.setOutRefundNo(orderNo+"E");
        request.setTotalFee(totalMoneyInt);
        request.setRefundFee(refundMoneyInt);
        if(OrderTypeEnum.PLATFORM.getType().equals(order.getType())){
            wxPayRefundResult = wxMaServiceSupply.playWxPayService().refund(request);
        }else if(OrderTypeEnum.POINT.getType().equals(order.getType())){
            wxPayRefundResult = wxMaServiceSupply.pointWxPayService().refund(request);
        }
        if(wxPayRefundResult==null){
            return false;
        }
        return true;
    }



    @Override
    public Boolean refund(String orderNo, BigDecimal totalMoney) throws WxPayException{
        return refund(orderNo,totalMoney,null);
    }

}
