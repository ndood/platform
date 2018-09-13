package com.fulu.game.core.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.utils.AppRouteFactory;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Dynamic;
import com.fulu.game.core.entity.DynamicPushMsg;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.AppPushMsgVO;
import com.fulu.game.core.entity.vo.DynamicCommentVO;
import com.fulu.game.core.service.DynamicPushMsgService;
import com.fulu.game.core.service.DynamicService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.push.MobileAppPushServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.DynamicCommentDao;
import com.fulu.game.core.entity.DynamicComment;
import com.fulu.game.core.service.DynamicCommentService;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Service("dynamicCommentService")
public class DynamicCommentServiceImpl extends AbsCommonService<DynamicComment,Integer> implements DynamicCommentService {

    @Autowired
	private DynamicCommentDao dynamicCommentDao;

    @Autowired
    private DynamicService dynamicService;

    @Autowired
    private UserService userService;

    @Autowired
    private MobileAppPushServiceImpl mobileAppPushService;

    @Autowired
    private DynamicPushMsgService dynamicPushMsgService;



    @Override
    public ICommonDao<DynamicComment, Integer> getDao() {
        return dynamicCommentDao;
    }

    /**
     * 评论接口
     *
     * @param dynamicCommentVO
     */
    @Override
    public void save(DynamicCommentVO dynamicCommentVO) {
        User user = userService.getCurrentUser();
        dynamicCommentVO.setFromUserId(user.getId());
        dynamicCommentVO.setFromUserGender(user.getGender());
        dynamicCommentVO.setFromUserHeadUrl(user.getHeadPortraitsUrl());
        dynamicCommentVO.setFromUserNickname(user.getNickname());
        if(dynamicCommentVO.getToUserId() != null && dynamicCommentVO.getToUserId().intValue() > 0){
            User toUser = userService.findById(dynamicCommentVO.getToUserId().intValue());
            dynamicCommentVO.setToUserGender(toUser.getGender());
            dynamicCommentVO.setToUserHeadUrl(toUser.getHeadPortraitsUrl());
            dynamicCommentVO.setToUserNickname(toUser.getNickname());
        }
        dynamicCommentVO.setCreateTime(new Date());
        dynamicCommentVO.setStatus(1);
        dynamicCommentDao.create(dynamicCommentVO);
        dynamicService.updateIndexFilesById(dynamicCommentVO.getDynamicId(), false, 0, 1,false);
        // 发送Jpush消息，通知被评论用户
        Dynamic dynamic = dynamicService.findById(dynamicCommentVO.getDynamicId());
        Map<String, String> extras = extras = AppRouteFactory.buildDynamicRoute(dynamicCommentVO.getDynamicId());
        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(dynamic.getUserId()).title("评论").alert(dynamicCommentVO.getContent()).extras(extras).build();
        //发送push消息
        mobileAppPushService.pushMsg(appPushMsgVO);
        // 保存push消息
        DynamicPushMsg dynamicPushMsg = new DynamicPushMsg();
        dynamicPushMsg.setDynamicId(dynamicCommentVO.getDynamicId());
        dynamicPushMsg.setFromUserId(user.getId());
        dynamicPushMsg.setFromUserHeadUrl(user.getHeadPortraitsUrl());
        dynamicPushMsg.setFromUserNickname(user.getNickname());
        dynamicPushMsg.setToUserId(dynamic.getUserId());
        //push消息类型（1：点赞；2：评论；3打赏）
        dynamicPushMsg.setPushType(2);
        dynamicPushMsg.setPushContent(dynamicCommentVO.getContent());
        dynamicPushMsg.setPushExtras(JSONObject.toJSONString(extras));
        dynamicPushMsg.setCreateTime(new Date());
        dynamicPushMsg.setUpdateTime(new Date());
        dynamicPushMsg.setIsDel(0);
        dynamicPushMsgService.create(dynamicPushMsg);
    }

    /**
     * 获取评论列表
     *
     * @param pageNum
     * @param pageSize
     * @param dynamicId
     * @return
     */
    @Override
    public PageInfo<DynamicCommentVO> list(Integer pageNum, Integer pageSize, Integer dynamicId) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        DynamicCommentVO dynamicCommentVO = new DynamicCommentVO();
        dynamicCommentVO.setDynamicId(dynamicId);
        List<DynamicCommentVO> list = dynamicCommentDao.list(dynamicCommentVO);
        return new PageInfo<>(list);
    }


}
