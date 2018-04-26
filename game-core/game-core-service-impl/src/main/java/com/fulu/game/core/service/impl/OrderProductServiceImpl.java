package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.OrderProductDao;
import com.fulu.game.core.entity.OrderProduct;
import com.fulu.game.core.service.OrderProductService;



@Service
public class OrderProductServiceImpl extends AbsCommonService<OrderProduct,Integer> implements OrderProductService {

    @Autowired
	private OrderProductDao orderProductDao;



    @Override
    public ICommonDao<OrderProduct, Integer> getDao() {
        return orderProductDao;
    }
	
}
