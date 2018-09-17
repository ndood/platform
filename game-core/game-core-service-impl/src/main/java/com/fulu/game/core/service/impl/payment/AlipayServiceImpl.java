package com.fulu.game.core.service.impl.payment;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.fulu.game.common.properties.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AlipayServiceImpl {


    private final Config configProperties;

    @Autowired
    public AlipayServiceImpl(Config configProperties) {
        this.configProperties = configProperties;
    }



    /**
     * 发起支付请求
     * @param model
     * @return
     */
    public String payRequest(AlipayTradeAppPayModel model) {
        log.info("发起支付请求,model:{}",model);
        Config.AlipayPay alipayPay = configProperties.getAlipayPay();
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", alipayPay.getAppId(), alipayPay.getAppPrivateKey(), "json", "utf-8", alipayPay.getAlipayPublicKey(), "RSA2");
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        request.setBizModel(model);
        request.setNotifyUrl(alipayPay.getPayNotifyUrl());
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            return JSONObject.toJSONString(response.getParams());
        } catch (AlipayApiException e) {
            log.error("支付宝发起支付请求异常:",e);
        }
        return null;
    }

}
