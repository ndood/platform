package com.fulu.game.thirdparty.fenqile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
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
    public String createOrder(FenqileOrderRequest fenqileOrderRequest) {
        String method = "fenqile.third.order.create";
        Map<String, Object> params = BeanUtil.beanToMap(fenqileOrderRequest, Boolean.TRUE, Boolean.TRUE);
        params.putAll(getConfMap(method));
        String sign = SignUtil.createSign(params, "MD5", getConfig().getPartnerKey());
        params.put("sign", sign);
        log.info("请求参数params:{}", HttpUtil.toParams(params));
        Map<String,Object> resultMap = null;
        try {
            String result = HttpUtil.post(BASE_API, params);
            if(result==null){
                throw new ApiErrorException("API请求错误");
            }
            log.info("请求结果result:{}", result);
            JSONObject jso = JSONObject.parseObject(result);
            if(jso.containsKey("error_response")){
                throw new ApiErrorException(result);
            }
            return result;
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }
    }


    @Override
    public void noticeModify(Integer noticeType,String noticeUrl){
        String method = "fenqile.third.notice.modify";
        Map<String,Object> params = getConfMap(method);
        params.put("notice_type",noticeType);
        params.put("notice_url",noticeUrl);
        String sign = SignUtil.createSign(params, "MD5", getConfig().getPartnerKey());
        params.put("sign", sign);
        log.info("请求参数params:{}", HttpUtil.toParams(params));
        try {
            String result = HttpUtil.post(BASE_API, params);
            if(result==null){
                throw new ApiErrorException("API请求错误");
            }
            log.info("请求结果result:{}", result);
            JSONObject jso = JSONObject.parseObject(result);
            if(jso.containsKey("error_response")){
                throw new ApiErrorException(result);
            }
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }
        log.info("修改分期乐订单通知url:{}",noticeUrl);
    }


    private Map<String,Object> getConfMap(String method){
        Map<String, Object> confMap = new HashMap<>();
        confMap.put("method", method);
        confMap.put("timestamp", new Date().getTime()/1000);
        confMap.put("format", "json");
        confMap.put("v", getConfig().getV());
        confMap.put("partner_id", getConfig().getPartnerId());
        return confMap;
    }

}
