package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.utils.CollectionUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.OrderMsgDao;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderMsg;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.OrderMsgVO;
import com.fulu.game.core.service.OrderMsgService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class OrderMsgServiceImpl extends AbsCommonService<OrderMsg, Integer> implements OrderMsgService {

    @Autowired
    private OrderMsgDao orderMsgDao;
    @Autowired
    private UserService userService;

    /**
     * 创建陪玩师订单消息
     *
     * @param order
     * @param message
     * @return
     */
    public OrderMsg createServerOrderMsg(Order order, String message) {
        return create(order.getServiceUserId(), order.getUserId(), order, message);
    }

    /**
     * 创建用户订单消息
     *
     * @param order
     * @param message
     * @return
     */
    public OrderMsg createUserOrderMsg(Order order, String message) {
        return create(order.getUserId(), order.getServiceUserId(), order, message);
    }

    public OrderMsg create(Integer userId, Integer oppUserId, Order order, String message) {
        User user = userService.findById(oppUserId);
        OrderMsg orderMsg = new OrderMsg();
        orderMsg.setOppHeadUrl(user.getHeadPortraitsUrl());
        orderMsg.setOppUsername(user.getNickname());
        orderMsg.setOrderNo(order.getOrderNo());
        orderMsg.setUserId(userId);
        orderMsg.setOrderTime(order.getBeginTime());
        orderMsg.setOrderName(order.getName());
        orderMsg.setOrderStatus(order.getStatus());
        orderMsg.setMessage(message);
        orderMsg.setCreateTime(new Date());
        orderMsg.setUpdateTime(new Date());
        create(orderMsg);
        return orderMsg;
    }


    public PageInfo<OrderMsgVO> list(int userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize,"id desc");
        OrderMsgVO param = new OrderMsgVO();
        param.setUserId(userId);
        List<OrderMsg> list = orderMsgDao.findByParameter(param);
        List<OrderMsgVO> orderMsgVOList = CollectionUtil.copyNewCollections(list, OrderMsgVO.class);
        for (OrderMsgVO orderMsgVO : orderMsgVOList) {
            orderMsgVO.setOrderStatusStr(OrderStatusEnum.getMsgByStatus(orderMsgVO.getOrderStatus()));
        }
        PageInfo page = new PageInfo(list);
        return page;
    }


    @Override
    public ICommonDao<OrderMsg, Integer> getDao() {
        return orderMsgDao;
    }


    /**
     * 用户评价订单
     * @param order
     */
    public void userCommentOrder(Order order) {
        this.createServerOrderMsg(order,"订单已评价");
        this.createUserOrderMsg(order,"订单已完成,已评价");
    }
}
