package com.fulu.game.thirdparty.fenqile.service;

import com.fulu.game.thirdparty.fenqile.entity.FenqileOrderRequest;

public interface FenqileOrderService {

    /**
     * 创建订单接口
     * @param fenqileOrderRequest
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T createOrder(FenqileOrderRequest fenqileOrderRequest, Class<T> clazz);


    /**
     * 修改通知回调
     * @param noticeType
     * @param noticeUrl
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T noticeModify(Integer noticeType,String noticeUrl,Class<T> clazz);
}
