package com.fulu.game.core.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.RoomDao;
import com.fulu.game.core.entity.Room;
import com.fulu.game.core.entity.RoomCategory;
import com.fulu.game.core.entity.vo.RoomVO;
import com.fulu.game.core.service.RoomCategoryService;
import com.fulu.game.core.service.RoomService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class RoomServiceImpl extends AbsCommonService<Room, Integer> implements RoomService {

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private RoomCategoryService roomCategoryService;

    @Override
    public ICommonDao<Room, Integer> getDao() {
        return roomDao;
    }


    @Override
    public PageInfo<RoomVO> findUsableRoomsByHot(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize, "sort desc");
        RoomVO param = new RoomVO();
        param.setIsActivate(true);
        param.setIsHot(true);
        param.setIsShow(true);
        List<Room> roomList = roomDao.findByParameter(param);
        List<RoomVO> result = new ArrayList<>();
        for (Room room : roomList) {
            //todo 获取房间人数
            RoomVO roomVO = new RoomVO();
            BeanUtil.copyProperties(room, roomVO);
            RoomCategory roomCategory = roomCategoryService.findById(room.getId());
            roomVO.setRoomCategoryName(roomCategory.getName());
            result.add(roomVO);
        }
        PageInfo page = new PageInfo(roomList);
        page.setList(result);
        return page;
    }


    @Override
    public PageInfo<RoomVO> findUsableRoomsByRoomCategory( int roomCategoryId) {
        PageHelper.startPage(1, 100, "sort desc");
        RoomVO param = new RoomVO();
        param.setIsActivate(true);
        param.setIsShow(true);
        param.setRoomCategoryId(roomCategoryId);
        List<Room> roomList = roomDao.findByParameter(param);
        List<RoomVO> result = new ArrayList<>();
        for (Room room : roomList) {
            RoomVO roomVO = new RoomVO();
            BeanUtil.copyProperties(room, roomVO);
            //todo 获取房间人数
            Integer people = room.getVirtualPeople();
            roomVO.setPeople(people);
            result.add(roomVO);
        }
        result.sort((RoomVO r1, RoomVO r2) -> r1.getPeople().compareTo(r2.getPeople()));
        PageInfo page = new PageInfo(roomList);
        page.setList(result);
        return page;
    }



    @Override
    public RoomVO findByOwner(int userId) {
        RoomVO param = new RoomVO();
        param.setIsActivate(true);
        param.setUserId(userId);
        List<Room> roomList = roomDao.findByParameter(param);
        if(roomList.isEmpty()){
            return null;
        }
        RoomVO roomVO = new RoomVO();
        BeanUtil.copyProperties(roomList.get(0),roomVO);
        return roomVO;
    }


}
