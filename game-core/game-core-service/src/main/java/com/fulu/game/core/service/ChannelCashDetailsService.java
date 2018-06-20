package com.fulu.game.core.service;

import com.fulu.game.core.entity.ChannelCashDetails;
import com.github.pagehelper.PageInfo;

import java.math.BigDecimal;

/**
 * 渠道商金额流水表
 *
 * @author yanbiao
 * @date 2018-06-13 15:33:45
 */
public interface ChannelCashDetailsService extends ICommonService<ChannelCashDetails, Integer> {

    /**
     * 管理员加款
     * @param channelId
     * @param money
     * @param remark
     * @return
     */
    ChannelCashDetails addCash(Integer channelId, BigDecimal money, String remark);

    /**
     * 渠道商下单扣款
     * @param channelId
     * @param money
     * @param orderNo
     * @return
     */
    ChannelCashDetails cutCash(Integer channelId, BigDecimal money, String orderNo);

    /**
     * 订单退款
     * @param channelId
     * @param money
     * @param orderNo
     * @return
     */
    ChannelCashDetails refundCash(Integer channelId, BigDecimal money, String orderNo);

    /**
     * 统计渠道商消费金额
     * @param channelId
     * @return
     */
    BigDecimal sumByChannelId(Integer channelId);

    /**
     * 加款列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<ChannelCashDetails> list(Integer pageNum, Integer pageSize,Integer channelId);

    /**
     * 管理员撤销部分金额(对渠道商扣款)
     * @param channelId
     * @param money
     * @param remark
     * @return
     */
    ChannelCashDetails cancelCash(Integer channelId, BigDecimal money, String remark);
}
