package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.OrderDealFileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.OrderDealFileDao;
import com.fulu.game.core.entity.OrderDealFile;
import com.fulu.game.core.service.OrderDealFileService;

import java.util.List;


@Service
public class OrderDealFileServiceImpl extends AbsCommonService<OrderDealFile,Integer> implements OrderDealFileService {

    @Autowired
	private OrderDealFileDao orderDealFileDao;



    @Override
    public ICommonDao<OrderDealFile, Integer> getDao() {
        return orderDealFileDao;
    }


    public List<OrderDealFile> findByOrderDeal(Integer orderDealId){
        OrderDealFileVO orderDealFileVO = new OrderDealFileVO();
        orderDealFileVO.setOrderDealId(orderDealId);
        return orderDealFileDao.findByParameter(orderDealFileVO);
    }

}
