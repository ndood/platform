package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.OrderDealDao;
import com.fulu.game.core.entity.OrderDeal;
import com.fulu.game.core.service.OrderDealService;



@Service
public class OrderDealServiceImpl extends AbsCommonService<OrderDeal,Integer> implements OrderDealService {

    @Autowired
	private OrderDealDao orderDealDao;



    @Override
    public ICommonDao<OrderDeal, Integer> getDao() {
        return orderDealDao;
    }
	
}
