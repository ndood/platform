package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.OrderMarketProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.OrderMarketProductDao;
import com.fulu.game.core.entity.OrderMarketProduct;
import com.fulu.game.core.service.OrderMarketProductService;

import java.util.List;


@Service
public class OrderMarketProductServiceImpl extends AbsCommonService<OrderMarketProduct,Integer> implements OrderMarketProductService {

    @Autowired
	private OrderMarketProductDao orderMarketProductDao;



    @Override
    public ICommonDao<OrderMarketProduct, Integer> getDao() {
        return orderMarketProductDao;
    }



    @Override
    public OrderMarketProduct findByOrderNo(String orderNo) {
        if(orderNo==null){
            return null;
        }
        OrderMarketProductVO param = new OrderMarketProductVO();
        param.setOrderNo(orderNo);
        List<OrderMarketProduct> orderMarketProductList = orderMarketProductDao.findByParameter(param);
        if(orderMarketProductList.isEmpty()){
            return null;
        }
        return orderMarketProductList.get(0);
    }
}
