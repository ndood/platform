package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.RoomOrderDao;
import com.fulu.game.core.entity.RoomOrder;
import com.fulu.game.core.service.RoomOrderService;



@Service
public class RoomOrderServiceImpl extends AbsCommonService<RoomOrder,Integer> implements RoomOrderService {

    @Autowired
	private RoomOrderDao roomOrderDao;



    @Override
    public ICommonDao<RoomOrder, Integer> getDao() {
        return roomOrderDao;
    }
	
}
