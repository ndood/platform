package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.OrderMarketProductDao;
import com.fulu.game.core.entity.OrderMarketProduct;
import com.fulu.game.core.service.OrderMarketProductService;



@Service
public class OrderMarketProductServiceImpl extends AbsCommonService<OrderMarketProduct,Integer> implements OrderMarketProductService {

    @Autowired
	private OrderMarketProductDao orderMarketProductDao;



    @Override
    public ICommonDao<OrderMarketProduct, Integer> getDao() {
        return orderMarketProductDao;
    }
	
}
