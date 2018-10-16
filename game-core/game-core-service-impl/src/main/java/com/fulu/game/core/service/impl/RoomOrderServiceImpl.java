package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.RoomEnum;
import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.RoomOrderDao;
import com.fulu.game.core.entity.RoomOrder;
import com.fulu.game.core.service.RoomOrderService;

import java.util.Date;


@Service
public class RoomOrderServiceImpl extends AbsCommonService<RoomOrder,Integer> implements RoomOrderService {

    @Autowired
	private RoomOrderDao roomOrderDao;



    @Override
    public ICommonDao<RoomOrder, Integer> getDao() {
        return roomOrderDao;
    }

    @Override
    public void createRoomGiftOrder(String roomNo, String orderNo) {
        RoomOrder roomOrder = new RoomOrder();
        roomOrder.setOrderNo(orderNo);
        roomOrder.setRoomNo(roomNo);
        roomOrder.setCreateTime(new Date());
        roomOrder.setType(RoomEnum.RoomOrderEnum.GIFT.getType());
        create(roomOrder);
    }

    @Override
    public void createRoomPlayOrder(String roomNo, String orderNo) {
        RoomOrder roomOrder = new RoomOrder();
        roomOrder.setOrderNo(orderNo);
        roomOrder.setRoomNo(roomNo);
        roomOrder.setCreateTime(new Date());
        roomOrder.setType(RoomEnum.RoomOrderEnum.ORDER.getType());
        create(roomOrder);
    }
}
