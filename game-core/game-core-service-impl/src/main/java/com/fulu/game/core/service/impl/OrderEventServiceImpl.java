package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.OrderEventTypeEnum;
import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.OrderEventDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.OrderDealVO;
import com.fulu.game.core.entity.vo.OrderEventVO;
import com.fulu.game.core.service.*;
import cn.hutool.core.bean.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Service
public class OrderEventServiceImpl extends AbsCommonService<OrderEvent, Integer> implements OrderEventService {

    @Autowired
    private OrderEventDao orderEventDao;
    @Autowired
    private OrderStatusDetailsService orderStatusDetailsService;
    @Autowired
    private OrderDealService orderDealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;


    @Override
    public ICommonDao<OrderEvent, Integer> getDao() {
        return orderEventDao;
    }


    public OrderEventVO getOrderEvent(Order order, User user,Integer type) {
        if (order == null) {
            return null;
        }
        OrderEventVO param = new OrderEventVO();
        param.setOrderNo(order.getOrderNo());
        param.setType(type);
        List<OrderEvent> orderEventList = orderEventDao.findByParameter(param);
        if (orderEventDao.findByParameter(param).isEmpty()) {
            return null;
        }
        Category category = categoryService.findById(order.getCategoryId());

        OrderEvent orderEvent = orderEventList.get(0);
        OrderEventVO orderEventVO = new OrderEventVO();
        BeanUtil.copyProperties(orderEvent, orderEventVO);
        orderEventVO.setCurrentOrderStatus(order.getStatus());
        orderEventVO.setNickname(user.getNickname());
        orderEventVO.setHeadUrl(user.getHeadPortraitsUrl());
        orderEventVO.setCategoryName(category.getName());
        orderEventVO.setCurrentOrderStatus(order.getStatus());
        orderEventVO.setCurrentOrderStatusStr(OrderStatusEnum.getMsgByStatus(order.getStatus()));
        orderEventVO.setCountDown(orderStatusDetailsService.getCountDown(order.getOrderNo(),order.getStatus()));
        List<OrderDealVO> orderDealVOList = orderDealService.findByOrderEventId(orderEvent.getId());
        for(OrderDealVO orderDealVO : orderDealVOList){
            User ouser = userService.findById(orderDealVO.getUserId());
            if(ouser!=null){
                orderDealVO.setHeadUrl(ouser.getHeadPortraitsUrl());
                orderDealVO.setNickname(ouser.getNickname());
            }
        }
        orderEventVO.setOrderDealList(orderDealVOList);
        return orderEventVO;
    }


    @Override
    public OrderEvent createCheckEvent(Order order, User applyUser, String remark, String... fileUrl) {
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderNo(order.getOrderNo());
        orderEvent.setApplyId(applyUser.getId());
        orderEvent.setUserId(order.getUserId());
        orderEvent.setOrderStatus(order.getStatus());
        orderEvent.setServiceUserId(order.getServiceUserId());
        orderEvent.setType(OrderEventTypeEnum.CHECK.getType());
        orderEvent.setCreateTime(new Date());
        orderEvent.setIsDel(false);
        create(orderEvent);
        OrderDeal orderDeal = new OrderDeal();
        orderDeal.setTitle("陪玩师提交验收订单");
        orderDeal.setType(OrderEventTypeEnum.CHECK.getType());
        orderDeal.setUserId(order.getServiceUserId());
        orderDeal.setRemark(remark);
        orderDeal.setOrderNo(order.getOrderNo());
        orderDeal.setOrderEventId(orderEvent.getId());
        orderDeal.setCreateTime(new Date());
        orderDealService.create(orderDeal, fileUrl);
        return orderEvent;
    }


    @Override
    public OrderEvent createConsult(Order order,
                                    User applyUser,
                                    int orderStatus,
                                    BigDecimal refundMoney) {
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderNo(order.getOrderNo());
        orderEvent.setApplyId(applyUser.getId());
        orderEvent.setRefundMoney(refundMoney);
        orderEvent.setOrderStatus(orderStatus);
        orderEvent.setUserId(order.getUserId());
        orderEvent.setServiceUserId(order.getServiceUserId());
        orderEvent.setType(OrderEventTypeEnum.CONSULT.getType());
        orderEvent.setCreateTime(new Date());
        orderEvent.setIsDel(false);
        create(orderEvent);
        return orderEvent;
    }

    @Override
    public OrderEvent createAppeal(Order order, User applyUser, String remark, String... fileUrl) {
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderNo(order.getOrderNo());
        orderEvent.setApplyId(applyUser.getId());
        orderEvent.setUserId(order.getUserId());
        orderEvent.setOrderStatus(order.getStatus());
        orderEvent.setServiceUserId(order.getServiceUserId());
        orderEvent.setRefundMoney(order.getActualMoney());
        orderEvent.setType(OrderEventTypeEnum.APPEAL.getType());
        orderEvent.setCreateTime(new Date());
        orderEvent.setIsDel(false);
        create(orderEvent);
        OrderDeal orderDeal = new OrderDeal();
        orderDeal.setTitle("申请了客服仲裁");
        orderDeal.setType(OrderEventTypeEnum.APPEAL.getType());
        orderDeal.setUserId(applyUser.getId());
        orderDeal.setRemark(remark);
        orderDeal.setOrderNo(order.getOrderNo());
        orderDeal.setOrderEventId(orderEvent.getId());
        orderDeal.setCreateTime(new Date());
        orderDealService.create(orderDeal, fileUrl);
        return orderEvent;
    }

    //取消协商
    @Override
    public void cancelConsult(Order order, User applyUser, OrderEvent orderEvent) {
        //创建取消协商留言
        OrderDeal orderDeal = new OrderDeal();
        if(applyUser==null){
            orderDeal.setTitle("取消协商");
            orderDeal.setRemark("系统自动取消协商订单");
            orderDeal.setUserId(order.getUserId());
        }else{
            orderDeal.setTitle("取消协商");
            orderDeal.setRemark("用户取消协商订单");
            orderDeal.setUserId(applyUser.getId());
        }
        orderDeal.setType(OrderEventTypeEnum.CONSULT.getType());
        orderDeal.setOrderNo(order.getOrderNo());
        orderDeal.setOrderEventId(orderEvent.getId());
        orderDeal.setCreateTime(new Date());
        orderDealService.create(orderDeal, null);
        //创建取消协商的订单状态详情
        orderStatusDetailsService.create(order.getOrderNo(), OrderStatusEnum.CONSULT_CANCEL.getStatus());


        Integer[] invalidStatus = new Integer[]{orderEvent.getOrderStatus(),
                                                OrderStatusEnum.CONSULTING.getStatus(),
                                                OrderStatusEnum.CONSULT_CANCEL.getStatus(),
                                                OrderStatusEnum.CONSULT_REJECT.getStatus()};

        //重置订单状态
        orderStatusDetailsService.resetOrderStatus(order.getOrderNo(), orderEvent.getOrderStatus(),invalidStatus);
        //删除申诉
        orderEvent.setIsDel(true);
        update(orderEvent);
        redisOpenService.setTimeInterval(CANCEL_CONSULT_LIMIT+order.getOrderNo(),2*60*60);
    }


    public OrderEvent findByOrderNoAndType(String orderNo,int type){
        OrderEventVO param = new OrderEventVO();
        param.setOrderNo(orderNo);
        param.setType(type);
        List<OrderEvent> orderEventList = orderEventDao.findByParameter(param);
        if (orderEventDao.findByParameter(param).isEmpty()) {
            return null;
        }
        return orderEventList.get(0);
    }

}
