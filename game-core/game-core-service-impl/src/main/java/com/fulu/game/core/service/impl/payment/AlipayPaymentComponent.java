package com.fulu.game.core.service.impl.payment;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeRefundApplyModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.fulu.game.common.enums.PayBusinessEnum;
import com.fulu.game.common.properties.Config;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.VirtualPayOrder;
import com.fulu.game.core.entity.payment.res.PayCallbackRes;
import com.fulu.game.core.entity.payment.res.PayRequestRes;
import com.fulu.game.core.entity.payment.model.PayCallbackModel;
import com.fulu.game.core.entity.payment.model.PayRequestModel;
import com.fulu.game.core.entity.payment.model.RefundModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
public class AlipayPaymentComponent implements PaymentComponent {


    private final Config configProperties;


    @Autowired
    public AlipayPaymentComponent(Config configProperties) {
        this.configProperties = configProperties;
    }


    @Override
    public PayRequestRes payRequest(PayRequestModel payRequestModel) {
        log.info("执行支付宝支付:{}",payRequestModel);
        PayRequestRes payRequestRes = new PayRequestRes(false);
        AlipayTradeAppPayModel model = null;
        if (payRequestModel.getPayBusinessEnum().equals(PayBusinessEnum.VIRTUAL_PRODUCT)) {
            model =buildAlipayRequest(payRequestModel.getVirtualPayOrder());
        } else {
            model = buildAlipayRequest(payRequestModel.getOrder());
        }
        String result = payRequest(payRequestModel.getPayBusinessEnum(), model);
        payRequestRes.setRequestParameter(result);
        return payRequestRes;
    }



    @Override
    public PayCallbackRes payCallBack(PayCallbackModel payCallbackModel) {
        log.info("调用支付宝回调方法:{}",payCallbackModel);
        PayCallbackRes payCallbackTO = new PayCallbackRes();
        try {
            boolean flag = AlipaySignature.rsaCheckV1(payCallbackModel.getAliPayParameterMap(), configProperties.getAlipayPay().getAlipayCallBackPublicKey(), "utf-8", "RSA2");
            payCallbackTO.setSuccess(flag);
            if (flag) {
                Map<String, String> result = payCallbackModel.getAliPayParameterMap();
                payCallbackTO.setOrderNO(result.get("out_trade_no"));
                payCallbackTO.setPayMoney(result.get("buyer_pay_amount"));
            }
        } catch (Exception e) {
            log.info("支付宝回调解析异常:", e);
        }
        return payCallbackTO;
    }




    @Override
    public boolean refund(RefundModel refundModel) {
        log.info("执行支付宝退款业务方法:{}",refundModel);
        if(new BigDecimal(0).compareTo(refundModel.getRefundMoney())<=0){
            return true;
        }
        AlipayTradeRefundApplyModel model = buildAlipayRefund(refundModel.getOrderNo(), refundModel.getRefundMoney().toPlainString());
        return alipayRefundRequest(model);
    }



    public AlipayTradeAppPayModel buildAlipayRequest(Order order) {
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody(order.getName());
        model.setSubject(order.getName());
        model.setOutTradeNo(order.getCouponNo());
        model.setTimeoutExpress("30m");
        model.setTotalAmount(order.getActualMoney().toPlainString());
        model.setProductCode("QUICK_MSECURITY_PAY");
        return model;
    }


    public AlipayTradeAppPayModel buildAlipayRequest(VirtualPayOrder virtualPayOrder) {
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody(virtualPayOrder.getName());
        model.setSubject(virtualPayOrder.getName());
        model.setOutTradeNo(virtualPayOrder.getOrderNo());
        model.setTimeoutExpress("30m");
        model.setTotalAmount(virtualPayOrder.getActualMoney().toPlainString());
        model.setProductCode("QUICK_MSECURITY_PAY");
        return model;
    }


    public AlipayTradeRefundApplyModel buildAlipayRefund(String orderNo, String refundAmount) {
        AlipayTradeRefundApplyModel model = new AlipayTradeRefundApplyModel();
        model.setOutRequestNo(orderNo);
        model.setRefundAmount(refundAmount);
        model.setOutRequestNo(orderNo + "E");
        model.setRefundReason(orderNo + "订单正常退款!");
        return model;
    }

    /**
     * 发起退款请求
     *
     * @param model
     * @return
     */
    public boolean alipayRefundRequest(AlipayTradeRefundApplyModel model) {
        log.info("发起退款请求,model:{}", model);
        Config.AlipayPay alipayPay = configProperties.getAlipayPay();
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(configProperties.getAlipayPay().getPayGateway(), alipayPay.getAppId(), alipayPay.getAppPrivateKey(), "json", "utf-8", alipayPay.getAlipayPublicKey(), "RSA2");
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            request.setBizModel(model);
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                return true;
            } else {
                log.error("支付宝退款失败错误信息:", response.getSubMsg());
            }
        } catch (Exception e) {
            log.error("支付宝发起退款异常:", e);
        }
        return false;
    }


    /**
     * 发起支付请求
     *
     * @param model
     * @return
     */
    public String payRequest(PayBusinessEnum payBusinessEnum, AlipayTradeAppPayModel model) {
        log.info("发起支付请求,model:{}", model);
        Config.AlipayPay alipayPay = configProperties.getAlipayPay();
        AlipayClient alipayClient = new DefaultAlipayClient(configProperties.getAlipayPay().getPayGateway(), alipayPay.getAppId(), alipayPay.getAppPrivateKey(), "json", "utf-8", alipayPay.getAlipayPublicKey(), "RSA2");
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        request.setBizModel(model);
        if (PayBusinessEnum.ORDER.equals(payBusinessEnum)) {
            request.setNotifyUrl(alipayPay.getPayOrderNotifyUrl());
        } else if (PayBusinessEnum.VIRTUAL_PRODUCT.equals(payBusinessEnum)) {
            request.setNotifyUrl(alipayPay.getPayVirtualProductNotifyUrl());
        }
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            log.info("支付宝responsebody{}",response.getBody());
            return response.getBody();
        } catch (AlipayApiException e) {
            log.error("支付宝发起支付请求异常:", e);
        }
        return null;
    }


}
