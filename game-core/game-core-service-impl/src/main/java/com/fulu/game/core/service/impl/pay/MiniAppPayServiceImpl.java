package com.fulu.game.core.service.impl.pay;

import cn.hutool.core.date.DateUtil;
import com.fulu.game.core.entity.Order;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Slf4j
public abstract class MiniAppPayServiceImpl extends PayServiceImpl<WxPayOrderNotifyResult> {

    /**
     * 构造微信支付请求request
     *
     * @param order 订单
     * @param ip    请求ip
     * @return 支付request
     */
    protected WxPayUnifiedOrderRequest buildWxPayRequest(Order order, String ip) {
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody(order.getName());
        orderRequest.setOutTradeNo(order.getOrderNo());
        orderRequest.setTotalFee((order.getActualMoney().multiply(new BigDecimal(100))).intValue());//元转成分
        orderRequest.setSpbillCreateIp(ip);
        orderRequest.setTimeStart(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        return orderRequest;
    }

    @Override
    public String getOrderNo(WxPayOrderNotifyResult result) {
        return result.getOutTradeNo();
    }

    @Override
    public String getTotal(WxPayOrderNotifyResult result) {
        return BaseWxPayResult.feeToYuan(result.getTotalFee());
    }

    @Override
    public boolean thirdRefund(String orderNo, Integer totalMoney, Integer refundMoney) throws WxPayException {
        WxPayRefundRequest request = new WxPayRefundRequest();
        request.setOutTradeNo(orderNo);
        request.setOutRefundNo(orderNo + "E");
        request.setTotalFee(totalMoney);
        request.setRefundFee(refundMoney);
        WxPayRefundResult wxPayRefundResult = refund(request);
        return wxPayRefundResult != null;
    }

    protected abstract WxPayRefundResult refund(WxPayRefundRequest wxPayRefundRequest) throws WxPayException;

}
