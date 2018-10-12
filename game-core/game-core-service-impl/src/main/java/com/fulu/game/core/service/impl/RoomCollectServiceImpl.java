package com.fulu.game.core.service.impl;


import com.fulu.game.common.exception.RoomException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.RoomCollectVO;
import com.fulu.game.core.entity.vo.RoomVO;
import com.fulu.game.core.service.RoomService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.RoomCollectDao;
import com.fulu.game.core.entity.RoomCollect;
import com.fulu.game.core.service.RoomCollectService;

import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class RoomCollectServiceImpl extends AbsCommonService<RoomCollect,Integer> implements RoomCollectService {

    @Autowired
	private RoomCollectDao roomCollectDao;
    @Autowired
    private RoomService roomService;

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


    @Override
    public RoomCollect userCollect(String roomNo, Integer userId,Boolean flag) {
        log.info("用户收藏房间:flag:{},roomNo:{},userId:{}",flag,roomNo,userId);
        RoomCollect roomCollect = findByRoomAndUser(roomNo,userId);
        if(flag){
            if(roomCollect==null){
                roomCollect = new RoomCollect();
                roomCollect.setRoomNo(roomNo);
                roomCollect.setUserId(userId);
                roomCollect.setCreateTime(new Date());
                roomCollect.setUpdateTime(new Date());
                create(roomCollect);
                return roomCollect;
            }else{
                throw new RoomException(RoomException.ExceptionCode.ROOM_COLLECT_REPEAT);
            }
        }else{
            if(roomCollect!=null){
                deleteById(roomCollect.getId());
            }
        }
        return null;
    }
}
