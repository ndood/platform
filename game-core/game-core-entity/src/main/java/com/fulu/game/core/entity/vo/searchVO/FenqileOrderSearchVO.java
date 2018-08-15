package com.fulu.game.core.entity.vo.searchVO;

/**
 * 分期乐订单查询VO
 *
 * @author Gong ZeChun
 * @date 2018/8/15 10:21
 */
public class FenqileOrderSearchVO extends OrderSearchVO {
    /**
     * 支付编号
     */
    private String paymentNo;

    /**
     * 对账状态（0：未对账（默认）；1：已对账）
     */
    private Integer reconStatus;
}
