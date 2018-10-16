package com.fulu.game.core.service.impl;

import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class MessageCenterServiceImpl implements MessageCenterService {

    @Autowired
    private ImService imService;
    @Autowired
    private AssignOrderSettingService assignOrderSettingService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RoomManageService roomManageService;
    @Autowired
    private UserService userService;

    public enum AdminImEnum{
        ASSIGN_ADMIN, //派单IM
        ORDER_ADMIN //派单IM
    }


    /**
     * 发送派单消息
     * @param roomAssignOrder
     */
    public void sendAssignOrderMsg(RoomAssignOrder roomAssignOrder) {
        //发送派单消息
        Set<Integer> userIdSet = assignOrderSettingService.findOpenAssignUserByCategoryId(roomAssignOrder.getCategoryId());
        List<RoomManage> list = roomManageService.findByRoomNo(roomAssignOrder.getRoomNo());
        for(RoomManage roomManage : list){
            userIdSet.add(roomManage.getUserId());
        }
        List<Integer> userIdList = new ArrayList(userIdSet);
        List<String> imIds = userService.findImIdsByUserIds(userIdList);
        //发送派单消息
        Map<String,Object> extMap = new HashMap<>();
        Category category = categoryService.findById(roomAssignOrder.getCategoryId());
        extMap.put("id",roomAssignOrder.getId());
        extMap.put("categoryName",category.getName());
        extMap.put("categoryIcon",category.getIcon());
        extMap.put("roomNo",roomAssignOrder.getRoomNo());
        extMap.put("content",roomAssignOrder.getContent());
        extMap.put("status",roomAssignOrder.getStatus());
        log.info("消息中心发送派单消息:{}",extMap);
        imService.sendMsgToImUser( imIds.toArray(new String[]{}),AdminImEnum.ASSIGN_ADMIN.name(),"CREATE",extMap);
    }


    public void updateSendAssignOrderMsg(RoomAssignOrder roomAssignOrder) {
        //发送派单消息
        Set<Integer> userIdSet = assignOrderSettingService.findOpenAssignUserByCategoryId(roomAssignOrder.getCategoryId());
        List<RoomManage> list = roomManageService.findByRoomNo(roomAssignOrder.getRoomNo());
        for(RoomManage roomManage : list){
            userIdSet.add(roomManage.getUserId());
        }
        List<Integer> userIdList = new ArrayList(userIdSet);
        List<String> imIds = userService.findImIdsByUserIds(userIdList);
        //发送派单消息

        Map<String,Object> extMap = new HashMap<>();
        Category category = categoryService.findById(roomAssignOrder.getCategoryId());
        extMap.put("id",roomAssignOrder.getId());
        extMap.put("categoryName",category.getName());
        extMap.put("categoryIcon",category.getIcon());
        extMap.put("roomNo",roomAssignOrder.getRoomNo());
        extMap.put("content",roomAssignOrder.getContent());
        extMap.put("status",roomAssignOrder.getStatus());
        log.info("消息中心发送派单消息:{}",extMap);
        imService.sendMsgToImUser( imIds.toArray(new String[]{}),AdminImEnum.ASSIGN_ADMIN.name(),"UPDATE",extMap);
    }



    @Override
    public void sendServerOrderMsg(Order order, String message) {
        User user = userService.findById(order.getUserId());
        User serverUser = userService.findById(order.getServiceUserId());
        Map<String,Object> extMap = new HashMap<>();
        extMap.put("orderNo",order.getOrderNo());
        extMap.put("icon",user.getHeadPortraitsUrl());
        extMap.put("orderName",order.getName());
        extMap.put("status",order.getStatus());
        extMap.put("statusStr", OrderStatusEnum.getMsgByStatus(order.getStatus()));
        extMap.put("date", DateUtil.format(new Date(),"MM月dd日 HH:mm"));
        log.info("消息中心发送陪玩师订单消息:{}",extMap);
        imService.sendMsgToImUser( new String[]{serverUser.getImId()},AdminImEnum.ASSIGN_ADMIN.name(),"CREATE",extMap);
    }


    @Override
    public void sendUserOrderMsg(Order order, String message) {
        User user = userService.findById(order.getUserId());
        User serverUser = userService.findById(order.getServiceUserId());
        Map<String,Object> extMap = new HashMap<>();
        extMap.put("orderNo",order.getOrderNo());
        extMap.put("icon",serverUser.getHeadPortraitsUrl());
        extMap.put("orderName",order.getName());
        extMap.put("status",order.getStatus());
        extMap.put("statusStr", OrderStatusEnum.getMsgByStatus(order.getStatus()));
        extMap.put("date", DateUtil.format(new Date(),"MM月dd日 HH:mm"));
        log.info("消息中心发送陪玩师用户消息:{}",extMap);
        imService.sendMsgToImUser( new String[]{user.getImId()},AdminImEnum.ASSIGN_ADMIN.name(),"CREATE",extMap);
    }


}
