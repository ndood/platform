package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.DetailsEnum;
import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.OrderMoneyDetailsDao;
import com.fulu.game.core.entity.OrderMoneyDetails;
import com.fulu.game.core.service.OrderMoneyDetailsService;

import java.util.Date;


@Service
public class OrderMoneyDetailsServiceImpl extends AbsCommonService<OrderMoneyDetails,Integer> implements OrderMoneyDetailsService {

    @Autowired
	private OrderMoneyDetailsDao orderMoneyDetailsDao;

    @Override
    public ICommonDao<OrderMoneyDetails, Integer> getDao() {
        return orderMoneyDetailsDao;
    }


    @Override
    public void create(String orderNo,
                       Integer userId,
                       DetailsEnum desc,
                       String money) {
        OrderMoneyDetails orderMoneyDetails = new OrderMoneyDetails();
        orderMoneyDetails.setOrderNo(orderNo);
        orderMoneyDetails.setUserId(userId);
        orderMoneyDetails.setDesc(desc.getMsg());
        orderMoneyDetails.setMoney(money);
        orderMoneyDetails.setCreateTime(new Date());
        create(orderMoneyDetails);
    }
}
