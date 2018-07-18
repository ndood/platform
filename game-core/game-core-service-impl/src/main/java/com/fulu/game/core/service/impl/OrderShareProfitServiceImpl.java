package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.OrderShareProfitDao;
import com.fulu.game.core.entity.OrderShareProfit;
import com.fulu.game.core.service.OrderShareProfitService;



@Service
public class OrderShareProfitServiceImpl extends AbsCommonService<OrderShareProfit,Integer> implements OrderShareProfitService {

    @Autowired
	private OrderShareProfitDao orderShareProfitDao;



    @Override
    public ICommonDao<OrderShareProfit, Integer> getDao() {
        return orderShareProfitDao;
    }
	
}
