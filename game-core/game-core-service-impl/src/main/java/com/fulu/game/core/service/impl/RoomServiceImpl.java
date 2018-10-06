package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.RoomDao;
import com.fulu.game.core.entity.Room;
import com.fulu.game.core.service.RoomService;



@Service
public class RoomServiceImpl extends AbsCommonService<Room,Integer> implements RoomService {

    @Autowired
	private RoomDao roomDao;



    @Override
    public ICommonDao<Room, Integer> getDao() {
        return roomDao;
    }
	
}
