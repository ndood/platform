package com.fulu.game.thirdparty.fenqile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.fulu.game.thirdparty.fenqile.entity.FenqileConfig;
import com.fulu.game.thirdparty.fenqile.entity.FenqileOrderRequest;
import com.fulu.game.thirdparty.fenqile.exception.ApiErrorException;
import com.fulu.game.thirdparty.fenqile.service.FenqileSdkOrderService;
import com.fulu.game.thirdparty.fenqile.util.SignUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FenqileSdkOrderServiceImpl implements FenqileSdkOrderService {


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

    /**
     * 更改回调和详情url
     * @param noticeType
     * @param noticeUrl
     */
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
        log.info("修改分期乐通知url:{}",noticeUrl);
    }


    @Override
    public void modifyPlatformUrl(){
        //修改订单通知回调
        noticeModify(1,getConfig().getOrderNoticeUrl());
        //修改订单详情页地址
        noticeModify(2,getConfig().getOrderDetailsUrl());
    }


    /**
     * 通知查询
     * @param noticeType
     */
    public void noticeQuery(Integer noticeType){
        String method = "fenqile.third.notice.query";
        Map<String,Object> params = getConfMap(method);
        params.put("notice_type",noticeType);
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
    }

    /**
     * 通知分期乐订单状态
     * @param orderNo
     * @param merchSaleStat
     */
    public void noticeFenqileOrderStatus(String orderNo,String fenqileOrderNo,Integer merchSaleStat){
        String method = "fenqile.third.order.notice";
        Map<String,Object> params = getConfMap(method);
        params.put("third_order_id",orderNo);
        params.put("order_id",fenqileOrderNo);
        params.put("merch_sale_state",merchSaleStat);
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
    }


    /**
     * 分期乐退款接口
     * @param orderNo
     * @param fenqileOrderNo
     */
    public boolean noticeFenqileRefund(String orderNo, String fenqileOrderNo, BigDecimal amount){
        String method = "fenqile.third.order.refund";
        Map<String,Object> params = getConfMap(method);
        params.put("third_order_id",orderNo);
        params.put("order_id",fenqileOrderNo);
        params.put("third_refund_id","E"+orderNo);
        params.put("amount",amount.toPlainString());
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
            return true;
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }
    }


    /**
     * 分期乐取消订单
     * @param orderNo
     * @param fenqileOrderNo
     * @return
     */
    public boolean cancelFenqileOrder(String orderNo,String fenqileOrderNo){
        try {
            noticeFenqileOrderStatus(orderNo,fenqileOrderNo,15);
            return true;
        }catch (Exception e){
            log.error("取消分期乐订单报错:",e);
            return false;
        }
    }

    /**
     * 分期乐玩成订单
     * @param orderNo
     * @param fenqileOrderNo
     * @return
     */
    public boolean completeFenqileOrder(String orderNo,String fenqileOrderNo){
        try {
            noticeFenqileOrderStatus(orderNo,fenqileOrderNo,13);
            return true;
        }catch (Exception e){
            log.error("取消分期乐订单报错:",e);
            return false;
        }
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
