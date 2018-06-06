package com.fulu.game.core.service;

import com.fulu.game.core.entity.OrderProduct;
import com.fulu.game.core.entity.vo.responseVO.OrderResVO;
import com.fulu.game.core.entity.vo.serachVO.OrderReqVO;
import com.github.pagehelper.PageInfo;

/**
 * 订单技能关联表
 * @author yanbiao
 * @date 2018-04-25 18:27:54
 */
public interface OrderProductService extends ICommonService<OrderProduct,Integer>{

    OrderProduct findByOrderNo(String orderNo);

    PageInfo<OrderResVO> list(OrderReqVO orderReqVO, Integer pageNum, Integer pageSize, String orderBy);
}
