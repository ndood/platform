package com.fulu.game.core.service;

import com.fulu.game.core.entity.VirtualProductOrder;
import com.fulu.game.core.entity.vo.VirtualProductOrderVO;

import java.util.List;


/**
 * 虚拟商品订单表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-30 10:04:09
 */
public interface VirtualProductOrderService extends ICommonService<VirtualProductOrder, Integer> {

    VirtualProductOrder sendGift(Integer targetUserId, Integer virtualProductId);

    String generateVirtualProductOrderNo();

    List<VirtualProductOrder> findByParameter(VirtualProductOrderVO virtualProductOrderVO);

    VirtualProductOrder findByOrderNo(String orderNo);
}
