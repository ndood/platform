package com.fulu.game.core.dao;

import com.fulu.game.core.entity.VirtualPayOrder;
import com.fulu.game.core.entity.vo.VirtualPayOrderVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 虚拟货币充值订单表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-09-03 16:10:17
 */
@Mapper
public interface VirtualPayOrderDao extends ICommonDao<VirtualPayOrder, Integer> {

    List<VirtualPayOrder> findByParameter(VirtualPayOrderVO virtualPayOrderVO);

    VirtualPayOrder findByOrderNo(String orderNo);
}
