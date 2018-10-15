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

    /**
     * 创建虚拟订单
     *
     * @param fromUserId       发起人id
     * @param targetUserId     接收人id
     * @param virtualProductId 虚拟商品id
     * @return 虚拟商品订单
     */
    VirtualProductOrder createVirtualOrder(int fromUserId, int targetUserId, int virtualProductId,int amount);
    
    
    boolean isAlreadyUnlock(Integer userId , Integer virtualProductId);
}
