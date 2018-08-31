package com.fulu.game.core.service;

import com.fulu.game.core.entity.VirtualProductOrder;


/**
 * 虚拟商品订单表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-30 10:04:09
 */
public interface VirtualProductOrderService extends ICommonService<VirtualProductOrder, Integer> {

    boolean sendGift(Integer fromUserId, Integer targetUserId, Integer virtualProductId);

    String generateVirtualProductOrderNo();
}
