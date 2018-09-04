package com.fulu.game.core.dao;

import com.fulu.game.core.entity.VirtualProductOrder;
import com.fulu.game.core.entity.vo.VirtualProductOrderVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 虚拟商品订单表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-30 10:04:09
 */
@Mapper
public interface VirtualProductOrderDao extends ICommonDao<VirtualProductOrder, Integer> {

    List<VirtualProductOrder> findByParameter(VirtualProductOrderVO virtualProductOrderVO);

    VirtualProductOrder findByOrderNo(String orderNo);
}
