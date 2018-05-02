package com.fulu.game.core.service.impl;

import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.UserCommentVO;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fulu.game.core.dao.UserCommentDao;
import com.fulu.game.core.entity.UserComment;
import com.fulu.game.core.service.UserCommentService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("/userCommentService")
public class UserCommentServiceImpl extends AbsCommonService<UserComment,Integer> implements UserCommentService {

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
     * @param commentVO
     */
    @Override
    public void save(UserCommentVO commentVO){
        User user = (User)SubjectUtil.getCurrentUser();
        Order order = orderService.findByOrderNo(commentVO.getOrderNo());
        int serverUserId = order.getServiceUserId();

        UserComment comment = new UserComment();
        if (commentVO.getRecordUser()){
            comment.setUserId(user.getId());
            //comment.setUserId(commentVO.getUserId());
        }
        comment.setServerUserId(serverUserId);
        comment.setScore(commentVO.getScore());
        comment.setOrderNo(commentVO.getOrderNo());
        comment.setContent(commentVO.getContent());
        comment.setCreateTime(new Date());
        comment.setUpdateTime(comment.getCreateTime());
        commentDao.create(comment);
        commentDao.callScoreAvgProc(comment.getId());
    }

    @Override
    public void put(UserCommentVO commentVO){
        User user = (User)SubjectUtil.getCurrentUser();
        UserComment comment = commentDao.findById(commentVO.getId());
        if (commentVO.getRecordUser()){
            comment.setUserId(user.getId());
            //comment.setUserId(commentVO.getUserId());
        }
        comment.setScore(commentVO.getScore());
        comment.setContent(commentVO.getContent());
        comment.setUpdateTime(new Date());
        commentDao.update(comment);
        commentDao.callScoreAvgProc(comment.getId());
    }

    @Override
    public PageInfo<UserCommentVO> findByServerId(int pageNum, int pageSize, Integer serverUserId) {
        UserCommentVO param = new UserCommentVO();
        param.setServerUserId(serverUserId);
        PageHelper.startPage(pageNum,pageSize,"create_time desc");
        List<UserCommentVO> commentVOList = commentDao.findVOByParameter(param);
        for(UserCommentVO userComment :commentVOList){
            if(userComment.getRecordUser()){
                userComment.setNickName("匿名用户");
            }else{
                User user = userService.findById(userComment.getUserId());
                userComment.setNickName(user.getNickname());
                userComment.setHeadUrl(user.getHeadPortraitsUrl());
            }
        }
        return new PageInfo<>(commentVOList);
    }

    @Override
    public UserComment findByOrderNo(String orderNo){
        UserCommentVO commentVO = new UserCommentVO();
        commentVO.setOrderNo(orderNo);
        List<UserComment> list = commentDao.findByParameter(commentVO);
        return list.size()>0?list.get(0):null;
    }
	
}
