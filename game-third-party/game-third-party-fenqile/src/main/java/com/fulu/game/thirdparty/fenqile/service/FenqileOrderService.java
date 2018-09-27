package com.fulu.game.thirdparty.fenqile.service;

import com.fulu.game.thirdparty.fenqile.entity.FenqileOrderRequest;

public interface FenqileOrderService {

    /**
     * 创建订单接口
     *
     * @param fenqileOrderRequest
     * @param clazz
     * @param <T>
     * @return
     */
    String createOrder(FenqileOrderRequest fenqileOrderRequest);


    /**
     * 修改通知回调
     *
     * @param noticeType
     * @param noticeUrl
     * @param clazz
     * @param <T>
     * @return
     */
    void noticeModify(Integer noticeType, String noticeUrl);


    void noticeQuery(Integer noticeType);

    /**
     * 取消分期乐订单
     *
     * @param orderNo
     * @param fenqileOrderNo
     * @return
     */
    boolean cancelFenqileOrder(String orderNo, String fenqileOrderNo);

    /**
     * 完成分期乐订单
     *
     * @param orderNo
     * @param fenqileOrderNo
     * @return
     */
    boolean completeFenqileOrder(String orderNo, String fenqileOrderNo);

    /**
     * 更改平台url
     */
    void modifyPlatformUrl();
}
