package com.fulu.game.core.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.exception.ParamsException;
import com.fulu.game.common.utils.AppRouteFactory;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.RewardDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.AppPushMsgVO;
import com.fulu.game.core.entity.vo.RewardVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.push.AppPushServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.entity.Reward;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("rewardService")
public class RewardServiceImpl extends AbsCommonService<Reward, Integer> implements RewardService {

    @Autowired
    private RewardDao rewardDao;

    @Autowired
    private VirtualProductOrderService virtualProductOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private DynamicService dynamicService;

    @Autowired
    private VirtualProductService virtualProductService;

    @Autowired
    private AppPushServiceImpl mobileAppPushService;

    @Autowired
    private DynamicPushMsgService dynamicPushMsgService;

    @Override
    public ICommonDao<Reward, Integer> getDao() {
        return rewardDao;
    }

    /**
     * 保存打赏记录
     * 生存订单号，插入订单表，然后在插入打赏记录表
     *
     * @param rewardVO
     * @return
     */
    @Override
    public void save(RewardVO rewardVO) {
        if (rewardVO == null) {
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }
        User user = userService.getCurrentUser();
        log.info("打赏记录：打赏用户Id：{}，接收奖励用户id：{}，礼物id：{}", user.getId(), rewardVO.getToUserId(), rewardVO.getGiftId());
        VirtualProductOrder virtualProductOrder = virtualProductOrderService.sendGift(rewardVO.getToUserId().intValue(), rewardVO.getGiftId().intValue());
        rewardVO.setFromUserGender(user.getGender());
        rewardVO.setFromUserHeadUrl(user.getHeadPortraitsUrl());
        rewardVO.setFromUserId(user.getId());
        rewardVO.setFromUserNickname(user.getNickname());
        User toUser = userService.findById(rewardVO.getToUserId().intValue());
        rewardVO.setToUserGender(toUser.getGender());
        rewardVO.setToUserHeadUrl(toUser.getHeadPortraitsUrl());
        rewardVO.setToUserNickname(toUser.getNickname());
        if(virtualProductOrder != null ){
            rewardVO.setOrderNo(virtualProductOrder.getOrderNo());
            VirtualProduct virtualProduct = virtualProductService.findById(rewardVO.getGiftId().intValue());
            rewardVO.setGiftUrl(virtualProduct.getObjectUrl());
            rewardVO.setGiftPrice(virtualProduct.getPrice());
            rewardVO.setCreateTime(new Date());
            rewardVO.setStatus(1);
            create(rewardVO);
            //修改动态打赏次数
            dynamicService.updateIndexFilesById(rewardVO.getResourceId(),true, 0, 0,false);
            Map<String, String> extras = extras = AppRouteFactory.buildDynamicRoute(rewardVO.getResourceId());
            String nikename = user.getNickname() == null || "".equals(user.getNickname()) ? user.getId() + "" : user.getNickname();
            AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(rewardVO.getToUserId()).title("动态消息").alert(nikename + "进行了打赏").extras(extras).build();
            //发送push消息
            mobileAppPushService.pushMsg(appPushMsgVO);
            // 保存push消息
            DynamicPushMsg dynamicPushMsg = new DynamicPushMsg();
            dynamicPushMsg.setDynamicId(rewardVO.getResourceId());
            dynamicPushMsg.setFromUserId(user.getId());
            dynamicPushMsg.setFromUserHeadUrl(user.getHeadPortraitsUrl());
            dynamicPushMsg.setFromUserNickname(nikename);
            dynamicPushMsg.setToUserId(rewardVO.getToUserId());
            //push消息类型（1：点赞；2：评论；3打赏）
            dynamicPushMsg.setPushType(3);
            dynamicPushMsg.setPushContent(virtualProduct.getName());
            dynamicPushMsg.setPushExtras(JSONObject.toJSONString(extras));
            dynamicPushMsg.setCreateTime(new Date());
            dynamicPushMsg.setUpdateTime(new Date());
            dynamicPushMsg.setIsDel(0);
            dynamicPushMsgService.create(dynamicPushMsg);
        } else {
            log.error("打赏记录：打赏用户Id：{}，接收奖励用户id：{}，礼物id：{}，调用虚拟商品订单接口失败", user.getId(), rewardVO.getToUserId(), rewardVO.getGiftId());
        }
    }

    /**
     * 获取打赏记录列表
     *
     * @param pageNum
     * @param pageSize
     * @param resourceId 打赏来源id
     * @param resourceType 打赏来源类型（1：动态打赏）
     * @return
     */
    @Override
    public PageInfo<Reward> list(Integer pageNum, Integer pageSize, Integer resourceId, Integer resourceType) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        RewardVO rewardVO = new RewardVO();
        rewardVO.setResourceId(resourceId);
        rewardVO.setResourceType(resourceType);
        List<Reward> list = rewardDao.findByParameter(rewardVO);
        return new PageInfo<>(list);
    }
}
