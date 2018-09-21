package com.fulu.game.play.service.impl;

import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.enums.WechatTemplateIdEnum;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.common.enums.WechatTemplateMsgTypeEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.UserInfoAuth;
import com.fulu.game.core.service.UserInfoAuthService;
import com.fulu.game.core.service.impl.push.MiniAppPushServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PlayMiniAppPushServiceImpl extends MiniAppPushServiceImpl {
    @Qualifier(value = "userInfoAuthServiceImpl")
    @Autowired
    private UserInfoAuthService userInfoAuthService;

    @Override
    protected void push(int userId, WechatTemplateMsgEnum wechatTemplateMsgEnum, String... replaces) {
        UserInfoAuth userInfoAuth = userInfoAuthService.findByUserId(userId);
        Boolean vestFlag = false;
        if (userInfoAuth != null) {
            vestFlag = userInfoAuth.getVestFlag() == null ? false : userInfoAuth.getVestFlag();
        }

        if (!vestFlag) {
            pushWechatTemplateMsg(
                    PlatformEcoEnum.PLAY.getType(),
                    userId,
                    WechatTemplateIdEnum.PLAY_LEAVE_MSG,
                    wechatTemplateMsgEnum.getPage().getPlayPagePath(),
                    wechatTemplateMsgEnum.getContent(),
                    replaces);
        }
    }

    @Override
    protected void push(int userId, Order order, WechatTemplateMsgEnum wechatTemplateMsgEnum,
                        WechatTemplateMsgTypeEnum wechatTemplateMsgTypeEnum, String... replaces) {
        UserInfoAuth userInfoAuth = userInfoAuthService.findByUserId(userId);
        Boolean vestFlag = false;
        if (userInfoAuth != null) {
            vestFlag = userInfoAuth.getVestFlag() == null ? false : userInfoAuth.getVestFlag();
        }

        if (!vestFlag) {
            pushServiceProcessMsg(
                    PlatformEcoEnum.PLAY.getType(),
                    userId,
                    order,
                    WechatTemplateIdEnum.PLAY_SERVICE_PROCESS_NOTICE,
                    wechatTemplateMsgEnum,
                    replaces);
        }
    }

    public void orderPay(Order order) {
        push(order.getServiceUserId(),
                order,
                WechatTemplateMsgEnum.ORDER_TOSERVICE_PAY,
                WechatTemplateMsgTypeEnum.SERVICE_PROCESS_NOTICE);
    }
}
