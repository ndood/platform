package com.fulu.game.core.service;


import com.fulu.game.common.enums.RoomRoleTypeEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.RoomManageDao;
import com.fulu.game.core.entity.RoomManage;
import com.fulu.game.core.entity.vo.RoomManageVO;
import com.fulu.game.core.service.impl.AbsCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class RoomManageServiceImpl extends AbsCommonService<RoomManage, Integer> implements RoomManageService {

    @Autowired
    private RoomManageDao roomManageDao;


    @Override
    public ICommonDao<RoomManage, Integer> getDao() {
        return roomManageDao;
    }


    @Override
    public RoomManage createManage(RoomRoleTypeEnum roomRoleTypeEnum, int userId, String roomNo) {
        RoomManage roomManage = new RoomManage();
        roomManage.setRoomNo(roomNo);
        roomManage.setRole(roomRoleTypeEnum.getType());
        roomManage.setUserId(userId);
        roomManage.setCreateTime(new Date());
        roomManage.setUpdateTime(new Date());
        create(roomManage);
        return roomManage;
    }


    @Override
    public void deleteManage(int userId, String roomNo) {
        RoomManage roomManage = findByUserAndRoomNo(userId, roomNo);
        if (roomManage != null) {
            deleteById(roomManage.getId());
        }
    }


    @Override
    public RoomManage findByUserAndRoomNo(int userId, String roomNo) {
        RoomManageVO param = new RoomManageVO();
        param.setUserId(userId);
        param.setRoomNo(roomNo);
        List<RoomManage> roomManageList = roomManageDao.findByParameter(param);
        if (roomManageList.isEmpty()) {
            return null;
        }
        return roomManageList.get(0);
    }


    @Override
    public List<RoomManage> findByRoomNo(String roomNo) {
        RoomManageVO param = new RoomManageVO();
        param.setRoomNo(roomNo);
        return roomManageDao.findByParameter(param);
    }
}
