package com.fulu.game.thirdparty.fenqile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.fulu.game.thirdparty.fenqile.entity.CodeSessionResult;
import com.fulu.game.thirdparty.fenqile.entity.FenqileConfig;
import com.fulu.game.thirdparty.fenqile.exception.ApiErrorException;
import com.fulu.game.thirdparty.fenqile.service.FenqileAuthService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FenqileAuthServiceImpl implements FenqileAuthService{

    protected FenqileConfig config;

    public FenqileConfig getConfig() {
        return config;
    }

    public void setConfig(FenqileConfig config) {
        this.config = config;
    }

    /**
     * 获取access_token
     * @param code
     * @return
     */

    public CodeSessionResult accessToken(String code){
        String baseUrl = "http://open.api.fenqile.com/auth/access_token";
        StringBuilder sb = new StringBuilder(baseUrl);
        sb.append("?client_id=").append(getConfig().getClientId());
        sb.append("&client_secret=").append(getConfig().getClientSecret());
        sb.append("&code=").append(code);
        String body = HttpUtil.get(sb.toString());
        if(body==null){
            throw new ApiErrorException("API请求错误");
        }
        log.info("请求结果result:{}", body);
        JSONObject jso = JSONObject.parseObject(body);
        if("ok".equals(jso.get("retmsg"))){
            CodeSessionResult codeSessionResult = BeanUtil.mapToBean(jso.getInnerMap(),CodeSessionResult.class, CopyOptions.create().setIgnoreCase(true).setIgnoreNullValue(true));
            return codeSessionResult;
        }
        throw new ApiErrorException(body);
    }

    /**
     *
     * @param openId
     * @param accessToken
     * @return
     */
    public Object getUserInfo(String openId,String accessToken){
        String baseUrl = "https://open.api.fenqile.com/auth/user_info.json";
        StringBuilder sb = new StringBuilder(baseUrl);
        sb.append("?openId=").append(openId);
        sb.append("&accessToken=").append(accessToken);
        String body = HttpUtil.get(sb.toString());
        if(body==null){
            throw new ApiErrorException("API请求错误");
        }
        log.info("请求结果result:{}", body);
        JSONObject jso = JSONObject.parseObject(body);
        if("ok".equals(jso.get("retmsg"))){
            CodeSessionResult codeSessionResult = BeanUtil.mapToBean(jso.getInnerMap(),CodeSessionResult.class, CopyOptions.create().setIgnoreCase(true).setIgnoreNullValue(true));
            return codeSessionResult;
        }
        throw new ApiErrorException(body);
    }



}
