package com.fulu.game.core.service.impl;

import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.OrderProductVO;
import com.fulu.game.core.entity.vo.requestVO.OrderReqVO;
import com.fulu.game.core.entity.vo.responseVO.OrderResVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fulu.game.core.dao.OrderProductDao;
import com.fulu.game.core.entity.OrderProduct;
import com.fulu.game.core.service.OrderProductService;

import java.util.List;

@Service("orderProductService")
public class OrderProductServiceImpl extends AbsCommonService<OrderProduct,Integer> implements OrderProductService {

    @Autowired
	private OrderProductDao orderProductDao;

    @Override
    public ICommonDao<OrderProduct, Integer> getDao() {
        return orderProductDao;
    }

    @Override
    public OrderProduct findByOrderNo(String orderNo) {
        OrderProductVO orderProductVO = new OrderProductVO();
        orderProductVO.setOrderNo(orderNo);
        List<OrderProduct> orderProductList =   orderProductDao.findByParameter(orderProductVO);
        if(orderProductList.isEmpty()){
            return null;
        }
        return orderProductList.get(0);
    }

    @Override
    public PageInfo<OrderResVO> list(OrderReqVO orderReqVO){
        PageHelper.startPage(orderReqVO.getPageNum(), orderReqVO.getPageSize(),orderReqVO.getOrderBy());
        List<OrderResVO> list = orderProductDao.findByUnionParam(orderReqVO);
        return new PageInfo(list);
    }
}
