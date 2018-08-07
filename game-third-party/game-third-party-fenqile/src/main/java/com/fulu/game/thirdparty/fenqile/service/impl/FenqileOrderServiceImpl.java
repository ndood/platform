package com.fulu.game.thirdparty.fenqile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.fulu.game.thirdparty.fenqile.entity.FenqileConfig;
import com.fulu.game.thirdparty.fenqile.entity.FenqileOrderRequest;
import com.fulu.game.thirdparty.fenqile.service.FenqileOrderService;
import com.fulu.game.thirdparty.fenqile.util.SignUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;
import java.util.Map;

@Slf4j
public class FenqileOrderServiceImpl implements FenqileOrderService {


    private String BASE_API  = "http://pop.api.fenqile.com/router/rest.js" ;

    protected FenqileConfig config;

    public FenqileConfig getConfig() {
        return config;
    }

    public void setConfig(FenqileConfig config) {
        this.config = config;
    }

    @Override
    public void createOrder(FenqileOrderRequest fenqileOrderRequest) {
        String method = "fenqile.third.order.create";
        Map<String, Object> params =  BeanUtil.beanToMap(fenqileOrderRequest,Boolean.TRUE,Boolean.TRUE);
        params.put("method",method);
        params.put("timestamp",new Date().getTime());
        params.put("format","json");
        params.put("v",getConfig().getV());
        params.put("partner_id",getConfig().getPartnerId());
        String sign = SignUtil.createSign(params,"MD5",getConfig().getPartnerKey());
        params.put("sign",sign);
        log.info("请求参数params:{}",params);


        String result =  HttpUtil.post(BASE_API,params);
        log.info("请求结果result:{}",result);
    }


}
