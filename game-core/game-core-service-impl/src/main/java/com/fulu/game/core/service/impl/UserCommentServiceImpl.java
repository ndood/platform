package com.fulu.game.core.service.impl;

import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.enums.UserScoreEnum;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.UserCommentDao;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserComment;
import com.fulu.game.core.entity.vo.UserCommentVO;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.UserCommentService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.aop.UserScore;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service("/userCommentService")
public class UserCommentServiceImpl extends AbsCommonService<UserComment, Integer> implements UserCommentService {

    @Autowired
    private UserCommentDao commentDao;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;


    @Override
    public ICommonDao<UserComment, Integer> getDao() {
        return commentDao;
    }

    /**
     * 新增评价逻辑：新增评价记录后更新当前平价分，并同步到user表score_avg字段
     *
     * @param commentVO
     */
    @Override
    @UserScore(type = UserScoreEnum.USER_COMMENT)
    public void save(UserCommentVO commentVO) {
        User user = userService.getCurrentUser();
        Order order = orderService.findByOrderNo(commentVO.getOrderNo());
        if (null == order) {
            throw new OrderException(order.getOrderNo(), "订单不存在!");
        }
        //只有待评价的订单才能评价
        if (!order.getStatus().equals(OrderStatusEnum.SYSTEM_COMPLETE.getStatus()) && !order.getStatus().equals(OrderStatusEnum.COMPLETE.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有待评价的订单才能评价!");
        }
        userService.isCurrentUser(order.getUserId());
        int serverUserId = order.getServiceUserId();
        UserComment comment = new UserComment();
        comment.setRecordUser(commentVO.getRecordUser());
        comment.setUserId(user.getId());
        comment.setServerUserId(serverUserId);
        comment.setScore(commentVO.getScore());
        comment.setOrderNo(commentVO.getOrderNo());
        comment.setContent(commentVO.getContent());
        comment.setCreateTime(new Date());
        comment.setUpdateTime(comment.getCreateTime());
        commentDao.create(comment);

        //更改订单状态
        order.setStatus(OrderStatusEnum.ALREADY_APPRAISE.getStatus());
        order.setUpdateTime(new Date());
        orderService.update(order);

        //commentDao.callScoreAvgProc(comment.getId());
        //查询陪玩师的评分平均值后更新score_avg字段
        BigDecimal scoreAvg = commentDao.findScoreAvgByServerUserId(serverUserId);
        comment.setScoreAvg(scoreAvg);
        commentDao.update(comment);
        User serverUser = userService.findById(serverUserId);
        if (null == serverUser) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }
        serverUser.setScoreAvg(scoreAvg);
        userService.update(serverUser);
    }


    @Override
    public PageInfo<UserCommentVO> findByServerId(int pageNum, int pageSize, Integer serverUserId) {
        UserCommentVO param = new UserCommentVO();
        param.setServerUserId(serverUserId);
        PageHelper.startPage(pageNum, pageSize, "create_time desc");
        List<UserCommentVO> commentVOList = commentDao.findVOByParameter(param);
        for (UserCommentVO userComment : commentVOList) {
            if (userComment.getRecordUser()) {
                userComment.setNickName("匿名用户");
            } else {
                User user = userService.findById(userComment.getUserId());
                userComment.setNickName(user.getNickname());
                userComment.setHeadUrl(user.getHeadPortraitsUrl());
            }
        }
        return new PageInfo<>(commentVOList);
    }

    @Override
    public UserComment findByOrderNo(String orderNo) {
        UserCommentVO commentVO = new UserCommentVO();
        commentVO.setOrderNo(orderNo);
        List<UserComment> list = commentDao.findByParameter(commentVO);
        return list.size() > 0 ? list.get(0) : null;
    }

}
