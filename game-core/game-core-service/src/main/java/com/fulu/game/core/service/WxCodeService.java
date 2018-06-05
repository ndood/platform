package com.fulu.game.core.service;

import me.chanjar.weixin.common.exception.WxErrorException;

public interface WxCodeService {
    /**
     * 生成带参数的小程序码图片地址
     * @param scene
     * @param page
     * @return aliyun url
     */
    String create(String scene, String page) throws WxErrorException;
}
