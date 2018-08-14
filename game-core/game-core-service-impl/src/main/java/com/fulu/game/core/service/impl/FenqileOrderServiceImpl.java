package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.FenqileOrderDao;
import com.fulu.game.core.entity.FenqileOrder;
import com.fulu.game.core.service.FenqileOrderService;



@Service
public class FenqileOrderServiceImpl extends AbsCommonService<FenqileOrder,Integer> implements FenqileOrderService {

    @Autowired
	private FenqileOrderDao fenqileOrderDao;



    @Override
    public ICommonDao<FenqileOrder, Integer> getDao() {
        return fenqileOrderDao;
    }
	
}
