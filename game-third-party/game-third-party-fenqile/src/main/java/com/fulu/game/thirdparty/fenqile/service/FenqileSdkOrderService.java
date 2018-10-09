package com.fulu.game.thirdparty.fenqile.service;

import com.fulu.game.thirdparty.fenqile.entity.FenqileOrderRequest;

import java.math.BigDecimal;

public interface FenqileSdkOrderService {

    /**
     * 创建订单接口
     *
     * @param fenqileOrderRequest
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
     * 分期乐部分退款
     * @param orderNo
     * @param fenqileOrderNo
     * @param amount
     */
    boolean noticeFenqileRefund(String orderNo, String fenqileOrderNo, BigDecimal amount);

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
