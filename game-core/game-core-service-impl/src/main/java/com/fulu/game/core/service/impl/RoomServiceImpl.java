package com.fulu.game.core.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.utils.CollectionUtil;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.RoomDao;
import com.fulu.game.core.entity.Room;
import com.fulu.game.core.entity.RoomCategory;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.RoomVO;
import com.fulu.game.core.service.RoomCategoryService;
import com.fulu.game.core.service.RoomManageService;
import com.fulu.game.core.service.RoomService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class RoomServiceImpl extends AbsCommonService<Room, Integer> implements RoomService {

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private RoomCategoryService roomCategoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoomManageService roomManageService;

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
            RoomVO roomVO =room2VO(room);
            result.add(roomVO);
        }
        PageInfo page = new PageInfo(roomList);
        page.setList(result);
        return page;
    }

    @Override
    public PageInfo<RoomVO> list(int pageNum, int pageSize, String name) {
        PageHelper.startPage(pageNum, pageSize, "sort desc");
        RoomVO param = new RoomVO();
        param.setName(name);
        List<Room> roomList = roomDao.findByParameter(param);
        PageInfo page = new PageInfo(roomList);
        List<RoomVO> roomVOList = new ArrayList<>();
        for(Room room : roomList ){
            RoomVO roomVO =room2VO(room);
            roomVOList.add(roomVO);
        }
        page.setList(roomVOList);
        return page;
    }



    public PageInfo<RoomVO>  findCollectRoomByUser(int pageNum,int pageSize,int userId){
        PageHelper.startPage(pageNum, pageSize);
        List<Room> roomList =roomDao.findCollectRoomByUser(userId);
        List<RoomVO> result = new ArrayList<>();
        for (Room room : roomList) {
            RoomVO roomVO = room2VO(room);
            result.add(roomVO);
        }
        PageInfo page = new PageInfo(roomList);
        page.setList(result);
        return page;
    }



    @Override
    public PageInfo<RoomVO> findUsableRoomsByRoomCategory(int pageNum, int pageSize,int roomCategoryId) {
        PageHelper.startPage(pageNum, pageSize, "sort desc");
        RoomVO param = new RoomVO();
        param.setIsActivate(true);
        param.setIsShow(true);
        param.setRoomCategoryId(roomCategoryId);
        List<Room> roomList = roomDao.findByParameter(param);
        List<RoomVO> result = new ArrayList<>();
        for (Room room : roomList) {
            RoomVO roomVO = room2VO(room);
            result.add(roomVO);
        }
        result.sort((RoomVO r1, RoomVO r2) -> r1.getPeople().compareTo(r2.getPeople()));
        PageInfo page = new PageInfo(roomList);
        page.setList(result);
        return page;
    }


    @Override
    public RoomVO findByOwner(int userId) {
        RoomVO roomVO = new RoomVO();
        BeanUtil.copyProperties(findByUser(userId), roomVO);
        return roomVO;
    }


    @Override
    public Room findByUser(int userId) {
        RoomVO param = new RoomVO();
        param.setIsActivate(true);
        param.setUserId(userId);
        List<Room> roomList = roomDao.findByParameter(param);
        if (roomList.isEmpty()) {
            return null;
        }
        return roomList.get(0);
    }

    @Override
    public RoomVO save(RoomVO roomVO) {
        roomVO.setUpdateTime(new Date());
        if (roomVO.getId() == null) {
            User user = userService.findByMobile( roomVO.getOwnerMobile());
            if(user==null){
                throw new ServiceErrorException("手机号不存在!");
            }
            roomVO.setUserId(user.getId());
            roomVO.setIsShow(true);
            roomVO.setIsLock(false);
            roomVO.setRoomNo(generateRoomNo());
            roomVO.setCreateTime(new Date());
            create(roomVO);

        } else {
            update(roomVO);
        }
        return roomVO;
    }


    public Room findByRoomNo(String roomNo) {
        if (roomNo == null) {
            return null;
        }
        RoomVO param = new RoomVO();
        param.setRoomNo(roomNo);
        List<Room> roomList = roomDao.findByParameter(param);
        if (roomList.isEmpty()) {
            return null;
        }
        return roomList.get(0);
    }


    /**
     * 生成房间号
     *
     * @return
     */
    private String generateRoomNo() {
        String roomNo = GenIdUtil.GetRoomNo();
        if (findByRoomNo(roomNo) == null) {
            return roomNo;
        } else {
            return generateRoomNo();
        }
    }


    /**
     * 房间实体转换成VO
     * @param room
     * @return
     */
    private RoomVO room2VO(Room room){
        RoomVO roomVO = new RoomVO();
        BeanUtil.copyProperties(room,roomVO);
        //设置房间分类
        RoomCategory roomCategory = roomCategoryService.findById(room.getRoomCategoryId());
        roomVO.setRoomCategoryName(roomCategory.getName());
        //todo 设置房间人数
        Integer people = room.getVirtualPeople();
        roomVO.setPeople(people);
        return roomVO;
    }




}
