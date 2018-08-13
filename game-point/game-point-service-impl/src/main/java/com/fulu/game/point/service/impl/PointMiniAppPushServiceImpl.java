package com.fulu.game.point.service.impl;

import com.fulu.game.common.enums.*;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.UserVO;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.UserTechAuthService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.core.service.impl.push.MiniAppPushServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PointMiniAppPushServiceImpl extends MiniAppPushServiceImpl {

    @Autowired
    private UserService userService;
    @Autowired
    private UserTechAuthService userTechAuthService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;




    @Override
    protected void push(int userId, WechatTemplateMsgEnum wechatTemplateMsgEnum,String... replaces) {
        pushWechatTemplateMsg(WechatEcoEnum.POINT.getType(), userId, WechatTemplateIdEnum.POINT_LEAVE_MSG, wechatTemplateMsgEnum.getPage().getPointPagePath(), wechatTemplateMsgEnum.getContent(),replaces);
    }


    public void orderPay(Order order){
        push(order.getServiceUserId(),WechatTemplateMsgEnum.POINT_TOSE_ORDER_RECEIVING);
    }


    /**
     * 推送上分订单通知
     *
     * @param order
     */
    public void pushPointOrder(Order order) {
        log.info("推送上分订单:order:{};", order);
        Category category = categoryService.findById(order.getCategoryId());
        //查询所有符合推送条件的用户
        List<UserTechAuth> userTechAuthList = userTechAuthService.findNormalByCategory(order.getCategoryId());
        List<Integer> userIds = new ArrayList<>();
        for (UserTechAuth userTechAuth : userTechAuthList) {
            userIds.add(userTechAuth.getUserId());
        }
        if (userIds.isEmpty()) {
            log.error("推送集市订单通知失败:没有符合条件的用户!");
            return;
        }

        List<UserVO> userList = userService.findVOByUserIds(userIds);
        for (UserVO user : userList) {
            if (!UserInfoAuthStatusEnum.VERIFIED.getType().equals(user.getUserInfoAuth()) || !UserStatusEnum.NORMAL.getType().equals(user.getStatus())) {
                continue;
            }
            //默认为1分钟
            Float pushTimeInterval = user.getPushTimeInterval();
            if (pushTimeInterval == null) {
                pushTimeInterval = 1F;
            }
            //数据库设置永不推送
            if (pushTimeInterval.equals(0F)) {
                log.info("推送集市订单:用户设置永不推送:user:{}", user);
                continue;
            }
            //时间间隔内已经推送过
            if (redisOpenService.hasKey(RedisKeyEnum.MARKET_ORDER_IS_PUSH.generateKey(user.getId()))) {
                log.info("推送集市订单:该时间间隔内不能推送:user:{}", user);
                continue;
            }
            //推送订单消息
            push(user.getId(), WechatTemplateMsgEnum.POINT_ORDER_PUSH, category.getName());
            Long expire = new BigDecimal(pushTimeInterval).multiply(new BigDecimal(60)).longValue();
            redisOpenService.set(RedisKeyEnum.MARKET_ORDER_IS_PUSH.generateKey(user.getId()), order.getOrderNo(), expire);
            log.info("推送集市订单完成:userInfoAuth:{},order:{}", user, order);
        }
    }



}
