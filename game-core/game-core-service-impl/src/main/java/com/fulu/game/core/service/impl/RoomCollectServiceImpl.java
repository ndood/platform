package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.RoomCollectVO;
import com.fulu.game.core.entity.vo.RoomVO;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.RoomCollectDao;
import com.fulu.game.core.entity.RoomCollect;
import com.fulu.game.core.service.RoomCollectService;

import java.util.List;


@Service
public class RoomCollectServiceImpl extends AbsCommonService<RoomCollect,Integer> implements RoomCollectService {

    @Autowired
	private RoomCollectDao roomCollectDao;


    @Override
    public ICommonDao<RoomCollect, Integer> getDao() {
        return roomCollectDao;
    }




    @Override
    public RoomCollect findByRoomAndUser(String roomNo, Integer userId) {
        RoomCollectVO param = new RoomCollectVO();
        param.setRoomNo(roomNo);
        param.setUserId(userId);
        List<RoomCollect> roomCollectList = roomCollectDao.findByParameter(param);
        if(roomCollectList.isEmpty()){
            return null;
        }
        return roomCollectList.get(0);
    }
}
