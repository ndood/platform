package com.fulu.game.core.service;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.service.impl.AbsCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.RoomManageDao;
import com.fulu.game.core.entity.RoomManage;
import com.fulu.game.core.service.RoomManageService;



@Service
public class RoomManageServiceImpl extends AbsCommonService<RoomManage,Integer> implements RoomManageService {

    @Autowired
	private RoomManageDao roomManageDao;



    @Override
    public ICommonDao<RoomManage, Integer> getDao() {
        return roomManageDao;
    }
	
}
