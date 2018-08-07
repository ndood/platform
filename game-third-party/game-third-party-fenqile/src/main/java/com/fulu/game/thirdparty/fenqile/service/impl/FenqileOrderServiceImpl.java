package com.fulu.game.thirdparty.fenqile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.fulu.game.thirdparty.fenqile.entity.FenqileConfig;
import com.fulu.game.thirdparty.fenqile.entity.FenqileOrderRequest;
import com.fulu.game.thirdparty.fenqile.exception.ApiErrorException;
import com.fulu.game.thirdparty.fenqile.service.FenqileOrderService;
import com.fulu.game.thirdparty.fenqile.util.SignUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FenqileOrderServiceImpl implements FenqileOrderService {


    private final String BASE_API = "http://pop.api.fenqile.com/router/rest.json";

    protected FenqileConfig config;

    public FenqileConfig getConfig() {
        return config;
    }

    public void setConfig(FenqileConfig config) {
        this.config = config;
    }

    @Override
    public <T> T createOrder(FenqileOrderRequest fenqileOrderRequest,Class<T> clazz) {
        String method = "fenqile.third.order.create";
        Map<String, Object> params = BeanUtil.beanToMap(fenqileOrderRequest, Boolean.TRUE, Boolean.TRUE);
        params.put("method", method);
        params.put("timestamp", new Date().getTime());
        params.put("format", "json");
        params.put("v", getConfig().getV());
        params.put("partner_id", getConfig().getPartnerId());
        String sign = SignUtil.createSign(params, "MD5", getConfig().getPartnerKey());
        params.put("sign", sign);
        log.info("请求参数params:{}", HttpUtil.toParams(params));

        Map<String,Object> resultMap = null;
        try {
            String result = HttpUtil.post(BASE_API, params);
            log.info("请求结果result:{}", result);
            JSONObject jso = JSONObject.parseObject(result);
            if(jso.containsKey("error_response")){
                throw new ApiErrorException(result);
            }
            resultMap = jso.getInnerMap();
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }
        return BeanUtil.mapToBean(resultMap,clazz,Boolean.TRUE);
    }


}
