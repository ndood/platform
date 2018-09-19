package com.fulu.game.app.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.enums.VirtualMoneyPriceEnum;
import com.fulu.game.common.exception.PayException;
import com.fulu.game.common.properties.Config;
import com.fulu.game.core.entity.AppstorePayDetail;
import com.fulu.game.core.entity.payment.model.PayCallbackModel;
import com.fulu.game.core.entity.payment.model.PayRequestModel;
import com.fulu.game.core.entity.payment.model.RefundModel;
import com.fulu.game.core.entity.payment.res.PayCallbackRes;
import com.fulu.game.core.entity.payment.res.PayRequestRes;
import com.fulu.game.core.service.AppstorePayDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class AppleStorePayService {


    private final Config configProperties;

    @Autowired
    public AppleStorePayService(Config configProperties) {
        this.configProperties = configProperties;
    }


    /**
     * 钻石充值
     * @param receiptData
     * @return
     */
    public AppstorePayDetail diamondPay(String receiptData) {
        AppstorePayDetail appstorePayDetail = null;
        HttpRequest httpRequest = HttpUtil.createPost(configProperties.getAppleStore().getCheckPayUrl());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("receipt-data", receiptData);
        httpRequest.body(jsonObject.toJSONString(), "application/json; charset=utf-8");
        HttpResponse response = httpRequest.execute();
        if (response.getStatus() == 200) {
            String bundleId ="";
            log.info("请求状态200苹果支付结果为:{}", response.body());
            JSONObject jsores = JSONObject.parseObject(response.body());
            if (jsores.get("status").equals(0)){
                JSONObject  receipt = jsores.getJSONObject("receipt");
                if(receipt.containsKey("in_app")){
                    bundleId = receipt.getString("bundle_id");
                    JSONArray inApp = receipt.getJSONArray("in_app");
                    String productId = inApp.getJSONObject(0).getString("product_id");
                    String originalTransactionId = inApp.getJSONObject(0).getString("original_transaction_id");
                    String transactionId = inApp.getJSONObject(0).getString("transaction_id");
                    Integer quantity = inApp.getJSONObject(0).getInteger("quantity");
                    String purchaseDate = inApp.getJSONObject(0).getString("purchase_date");
                    appstorePayDetail = new AppstorePayDetail(transactionId,originalTransactionId,productId,quantity,purchaseDate);
                }else{
                    bundleId = receipt.getString("bid");
                    String productId = receipt.getString("product_id");
                    String originalTransactionId = receipt.getString("original_transaction_id");
                    String transactionId = receipt.getString("transaction_id");
                    Integer quantity = receipt.getInteger("quantity");
                    String purchaseDate = receipt.getString("purchase_date");
                    appstorePayDetail = new AppstorePayDetail(transactionId,originalTransactionId,productId,quantity,purchaseDate);
                }
            }
            if(!"com.peiwan.GamePlay".equals(bundleId)){
                throw new PayException(PayException.ExceptionCode.APPLE_STORE_CHECK_ERROR);
            }

        }
        log.info("请求状态出错:{}", response.body());
        return appstorePayDetail;
    }


}
