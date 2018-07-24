package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.OrderPointProductDao;
import com.fulu.game.core.entity.OrderPointProduct;
import com.fulu.game.core.service.OrderPointProductService;



@Service
public class OrderPointProductServiceImpl extends AbsCommonService<OrderPointProduct,Integer> implements OrderPointProductService {

    @Autowired
	private OrderPointProductDao orderPointProductDao;



    @Override
    public ICommonDao<OrderPointProduct, Integer> getDao() {
        return orderPointProductDao;
    }
	
}
