package com.fulu.game.core.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.exception.ParamsException;
import com.fulu.game.common.utils.AppRouteFactory;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Dynamic;
import com.fulu.game.core.entity.DynamicPushMsg;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.AppPushMsgVO;
import com.fulu.game.core.entity.vo.DynamicLikeVO;
import com.fulu.game.core.search.component.DynamicSearchComponent;
import com.fulu.game.core.service.DynamicPushMsgService;
import com.fulu.game.core.service.DynamicService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.push.MobileAppPushServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.DynamicLikeDao;
import com.fulu.game.core.entity.DynamicLike;
import com.fulu.game.core.service.DynamicLikeService;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Service("dynamicLikeService")
public class DynamicLikeServiceImpl extends AbsCommonService<DynamicLike,Integer> implements DynamicLikeService {

    @Autowired
	private DynamicLikeDao dynamicLikeDao;


    @Autowired
    private UserService userService;

    @Autowired
    private DynamicService dynamicService;

    @Autowired
    private MobileAppPushServiceImpl mobileAppPushService;

    @Autowired
    private DynamicPushMsgService dynamicPushMsgService;


    @Override
    public ICommonDao<DynamicLike, Integer> getDao() {
        return dynamicLikeDao;
    }

    /**
     * 点赞接口
     *
     * @param dynamicLikeVO
     */
    @Override
    public void save(DynamicLikeVO dynamicLikeVO) {
        if(dynamicLikeVO == null){
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }
        Integer status = dynamicLikeVO.getStatus();
        if(status != null && status == 0 ){

        }
        dynamicLikeVO.setStatus(null);
        User user = userService.getCurrentUser();
        dynamicLikeVO.setFromUserId(user.getId());
        List<DynamicLike> list = dynamicLikeDao.findByParameter(dynamicLikeVO);
        dynamicLikeVO.setStatus(status);
        dynamicLikeVO.setFromUserHeadUrl(user.getHeadPortraitsUrl());
        dynamicLikeVO.setFromUserNickname(user.getNickname());

        if(list != null && !list.isEmpty()){
            dynamicLikeVO.setId(list.get(0).getId());
            dynamicLikeDao.update(dynamicLikeVO);
        } else {
            dynamicLikeVO.setCreateTime(new Date());
            create(dynamicLikeVO);
        }
        // TODO shijiaoyun 修改动态信息的点赞次数（以及ES中存是否已点赞信息：likeUserId）
        if(status != null && status == 0){//取消点赞
            dynamicService.updateIndexFilesById(dynamicLikeVO.getDynamicId(), false, -1, 0,false);
        } else {//点赞
            dynamicService.updateIndexFilesById(dynamicLikeVO.getDynamicId(), false, 1, 0,false);
            Dynamic dynamic = dynamicService.findById(dynamicLikeVO.getDynamicId());
            // 推送push消息，以及点赞
            Map<String, String> extras = extras = AppRouteFactory.buildDynamicRoute(dynamicLikeVO.getDynamicId());
            String content = "点了一个赞";
            AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(dynamic.getUserId()).title("动态消息").alert(user.getNickname() + "进行了点赞").extras(extras).build();
            //发送push消息
            mobileAppPushService.pushMsg(appPushMsgVO);
            // 保存push消息
            DynamicPushMsg dynamicPushMsg = new DynamicPushMsg();
            dynamicPushMsg.setDynamicId(dynamicLikeVO.getDynamicId());
            dynamicPushMsg.setFromUserId(user.getId());
            dynamicPushMsg.setFromUserHeadUrl(user.getHeadPortraitsUrl());
            dynamicPushMsg.setFromUserNickname(user.getNickname());
            dynamicPushMsg.setToUserId(dynamic.getUserId());
            //push消息类型（1：点赞；2：评论；3打赏）
            dynamicPushMsg.setPushType(1);
            dynamicPushMsg.setPushContent(content);
            dynamicPushMsg.setPushExtras(JSONObject.toJSONString(extras));
            dynamicPushMsg.setCreateTime(new Date());
            dynamicPushMsg.setUpdateTime(new Date());
            dynamicPushMsg.setIsDel(0);
            dynamicPushMsgService.create(dynamicPushMsg);
        }

    }

    /**
     * 获取点赞记录列表接口
     *
     * @param pageNum
     * @param pageSize
     * @param dynamicId
     * @return
     */
    @Override
    public PageInfo<DynamicLike> list(Integer pageNum, Integer pageSize, Integer dynamicId) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        DynamicLikeVO dynamicLikeVO = new DynamicLikeVO();
        dynamicLikeVO.setDynamicId(dynamicId);
        dynamicLikeVO.setStatus(1);
        List<DynamicLike> list = dynamicLikeDao.findByParameter(dynamicLikeVO);
        return new PageInfo<>(list);
    }
}
