package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.OrderProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.OrderProductDao;
import com.fulu.game.core.entity.OrderProduct;
import com.fulu.game.core.service.OrderProductService;

import java.util.List;


@Service
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
}
