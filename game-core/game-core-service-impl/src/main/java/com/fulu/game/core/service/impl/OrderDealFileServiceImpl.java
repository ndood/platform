package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.OrderDealFileDao;
import com.fulu.game.core.entity.OrderDealFile;
import com.fulu.game.core.service.OrderDealFileService;



@Service
public class OrderDealFileServiceImpl extends AbsCommonService<OrderDealFile,Integer> implements OrderDealFileService {

    @Autowired
	private OrderDealFileDao orderDealFileDao;



    @Override
    public ICommonDao<OrderDealFile, Integer> getDao() {
        return orderDealFileDao;
    }
	
}
