package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.OrderPointProductVO;
import com.fulu.game.core.entity.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.OrderPointProductDao;
import com.fulu.game.core.entity.OrderPointProduct;
import com.fulu.game.core.service.OrderPointProductService;

import java.util.List;


@Service
public class OrderPointProductServiceImpl extends AbsCommonService<OrderPointProduct,Integer> implements OrderPointProductService {

    @Autowired
	private OrderPointProductDao orderPointProductDao;

    @Override
    public ICommonDao<OrderPointProduct, Integer> getDao() {
        return orderPointProductDao;
    }


    @Override
    public OrderPointProduct findByOrderNo(String orderNo) {
        if(orderNo==null){
            return null;
        }
        OrderPointProductVO param = new OrderPointProductVO();
        param.setOrderNo(orderNo);
        List<OrderPointProduct> orderPointProduct = orderPointProductDao.findByParameter(param);
        if(orderPointProduct.isEmpty()){
            return null;
        }
        return  orderPointProduct.get(0);
    }


}
