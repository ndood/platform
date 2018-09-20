package com.fulu.game.core.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserComment;
import com.fulu.game.core.entity.vo.ServerCommentVO;
import com.fulu.game.core.entity.vo.UserCommentVO;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.ServerCommentDao;
import com.fulu.game.core.entity.ServerComment;
import com.fulu.game.core.service.ServerCommentService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Service
public class ServerCommentServiceImpl extends AbsCommonService<ServerComment,Integer> implements ServerCommentService {

    @Autowired
	private ServerCommentDao serverCommentDao;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;



    @Override
    public ICommonDao<ServerComment, Integer> getDao() {
        return serverCommentDao;
    }

    /**
     * 保存评论
     *
     * @param serverCommentVO
     */
    @Override
    public void save(ServerCommentVO serverCommentVO) {
        User user = userService.getCurrentUser();
        Order order = orderService.findByOrderNo(serverCommentVO.getOrderNo());
        if (null == order) {
            throw new OrderException(order.getOrderNo(), "订单不存在!");
        }
        //只有待评价的订单才能评价
        if (!order.getStatus().equals(OrderStatusEnum.SYSTEM_COMPLETE.getStatus()) &&
                !order.getStatus().equals(OrderStatusEnum.COMPLETE.getStatus()) &&
                !order.getStatus().equals(OrderStatusEnum.ALREADY_APPRAISE.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有待评价的订单才能评价!");
        }
        Integer userId = order.getUserId();
        userService.isCurrentUser(order.getServiceUserId());
        serverCommentVO.setUserId(userId);
        serverCommentVO.setServerUserId(user.getId());
        serverCommentVO.setCreateTime(new Date());
        serverCommentDao.create(serverCommentVO);
        BigDecimal serverScoreAvg = serverCommentDao.findScoreAvgByUserId(userId);
        serverCommentVO.setScoreAvg(serverScoreAvg);
        serverCommentVO.setUpdateTime(new Date());
        serverCommentDao.update(serverCommentVO);
        User updateUser = userService.findById(userId);
        if (null == updateUser) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }
        updateUser.setServerScoreAvg(serverScoreAvg);
        userService.update(updateUser);
    }

    /**
     * 查询评价信息
     *
     * @param orderNo
     * @return
     */
    @Override
    public ServerCommentVO findByOrderNo(String orderNo) {
        Order order = orderService.findByOrderNo(orderNo);
        if (null == order) {
            throw new OrderException(order.getOrderNo(), "订单不存在!");
        }
        User user = userService.findById(order.getUserId());
        if (null == user) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }
        ServerCommentVO serverCommentVO = new ServerCommentVO();
        serverCommentVO.setOrderNo(orderNo);
        List<ServerComment> list = serverCommentDao.findByParameter(serverCommentVO);
        ServerComment serverComment = (list != null && list.size() > 0) ? list.get(0) : null;
        if(serverComment == null){
            serverComment = new ServerComment();
            serverComment.setOrderNo(orderNo);
        }
        BeanUtil.copyProperties(serverComment,serverCommentVO);
        serverCommentVO.setNickname(user.getNickname());
        serverCommentVO.setAge(user.getAge());
        serverCommentVO.setGender(user.getGender());
        serverCommentVO.setHeadPortraitsUrl(user.getHeadPortraitsUrl());
        serverCommentVO.setImId(user.getImId());
        return serverCommentVO;
    }
}
