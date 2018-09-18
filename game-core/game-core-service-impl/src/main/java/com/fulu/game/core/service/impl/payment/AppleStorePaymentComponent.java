package com.fulu.game.core.service.impl.payment;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.enums.VirtualMoneyPriceEnum;
import com.fulu.game.common.properties.Config;
import com.fulu.game.core.entity.payment.model.PayCallbackModel;
import com.fulu.game.core.entity.payment.model.PayRequestModel;
import com.fulu.game.core.entity.payment.model.RefundModel;
import com.fulu.game.core.entity.payment.res.PayCallbackRes;
import com.fulu.game.core.entity.payment.res.PayRequestRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AppleStorePaymentComponent implements PaymentComponent{



    private final Config configProperties;



    @Autowired
    public AppleStorePaymentComponent(Config configProperties) {
        this.configProperties = configProperties;
    }


    @Override
    public PayRequestRes payRequest(PayRequestModel paymentVO) {
        return null;
    }

    @Override
    public PayCallbackRes payCallBack(PayCallbackModel payCallbackVO) {
        return null;
    }

    @Override
    public boolean refund(RefundModel refundVO) {
        return false;
    }


    /**
     * 钻石充值
     * @param receiptData
     * @return
     */
    public int diamondPay(String receiptData) {
        HttpRequest httpRequest = HttpUtil.createPost(configProperties.getAppleStore().getCheckPayUrl());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("receipt-data", receiptData);
        httpRequest.body(jsonObject.toJSONString(), "application/json; charset=utf-8");
        HttpResponse response = httpRequest.execute();
        int diamond = 0;
        if (response.getStatus() == 200) {
            log.info("请求状态200苹果支付结果为:{}", response.body());
            JSONObject jsores = JSONObject.parseObject(response.body());
            if (jsores.get("status").equals(0)){
                String productId = null;
                JSONObject  receipt = jsores.getJSONObject("receipt");
                if(receipt.containsKey("in_app")){
                    JSONArray inApp = receipt.getJSONArray("in_app");
                    productId = inApp.getJSONObject(0).getString("product_id");
                }else{
                    productId = receipt.getString("product_id");
                }
                if(productId!=null){
                    return VirtualMoneyPriceEnum.getNumberByPriceStr(productId);
                }
            }
        }
        log.info("请求状态出错:{}", response.body());
        return diamond;
    }


}
