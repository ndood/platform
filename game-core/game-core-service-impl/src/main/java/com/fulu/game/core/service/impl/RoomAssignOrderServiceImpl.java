package com.fulu.game.core.service.impl;


import com.fulu.game.common.utils.CollectionUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.RoomAssignOrderDao;
import com.fulu.game.core.entity.RoomAssignOrder;
import com.fulu.game.core.entity.bo.RoomAssignOrderBO;
import com.fulu.game.core.entity.vo.RoomAssignOrderVO;
import com.fulu.game.core.service.RoomAssignOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class RoomAssignOrderServiceImpl extends AbsCommonService<RoomAssignOrder, Integer> implements RoomAssignOrderService {

    @Autowired
    private RoomAssignOrderDao roomAssignOrderDao;


    @Override
    public ICommonDao<RoomAssignOrder, Integer> getDao() {
        return roomAssignOrderDao;
    }


    /**
     * 创建派单消息
     *
     * @param roomNo
     * @param userId
     * @param categoryId
     * @param content
     */
    @Override
    public void create(String roomNo, Integer userId, Integer categoryId, String content) {
        RoomAssignOrder roomAssignOrder = new RoomAssignOrder();
        roomAssignOrder.setCategoryId(categoryId);
        roomAssignOrder.setRoomNo(roomNo);
        roomAssignOrder.setContent(content);
        roomAssignOrder.setRoomManagerUserId(userId);
        roomAssignOrder.setStatus(true);
        roomAssignOrder.setCreateTime(new Date());
        roomAssignOrder.setUpdateTime(new Date());
        log.info("[{}]房间创建了一个派单消息:roomAssignOrder:{}", roomAssignOrder);
        create(roomAssignOrder);
    }


    /**
     * 完成房间的派单
     */
    public void finishRoomAssignOrder(String roomNo, Integer userId) {
        log.info("[{}]房间完成一个派单:userId:{}", roomNo, userId);
        RoomAssignOrderBO param = new RoomAssignOrderBO();
        param.setRoomNo(roomNo);
        param.setStatus(true);
        List<RoomAssignOrder> roomAssignOrderList = roomAssignOrderDao.findByParameter(param);
        for (RoomAssignOrder roomAssignOrder : roomAssignOrderList) {
            roomAssignOrder.setStatus(false);
            roomAssignOrder.setUpdateTime(new Date());
            update(roomAssignOrder);
        }

    }


    @Override
    public List<RoomAssignOrderVO> unfinishedRoomAssignOrder(String roomNo, Integer userId) {
        RoomAssignOrderBO param = new RoomAssignOrderBO();
        param.setRoomNo(roomNo);
        param.setStatus(false);
        List<RoomAssignOrder> roomAssignOrderList = roomAssignOrderDao.findByParameter(param);
        return CollectionUtil.copyNewCollections(roomAssignOrderList, RoomAssignOrderVO.class);
    }

}