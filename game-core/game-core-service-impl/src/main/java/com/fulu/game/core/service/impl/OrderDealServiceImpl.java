package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.OrderDealFile;
import com.fulu.game.core.service.OrderDealFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.OrderDealDao;
import com.fulu.game.core.entity.OrderDeal;
import com.fulu.game.core.service.OrderDealService;

import java.util.Date;


@Service
public class OrderDealServiceImpl extends AbsCommonService<OrderDeal,Integer> implements OrderDealService {

    @Autowired
	private OrderDealDao orderDealDao;
    @Autowired
    private OrderDealFileService orderDealFileService;


    @Override
    public ICommonDao<OrderDeal, Integer> getDao() {
        return orderDealDao;
    }

    @Override
    public void create(String orderNo, Integer type, String remark, String... fileUrls) {
        OrderDeal orderDeal = new OrderDeal();
        orderDeal.setOrderNo(orderNo);
        orderDeal.setType(type);
        orderDeal.setRemark(remark);
        orderDeal.setCreateTime(new Date());
        create(orderDeal);
        for(String url : fileUrls){
            OrderDealFile orderDealFile = new OrderDealFile();
            orderDealFile.setFileUrl(url);
            orderDealFile.setOrderDealId(orderDeal.getId());
            orderDealFile.setCreateTime(new Date());
            orderDealFileService.create(orderDealFile);
        }
    }
}