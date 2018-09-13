package com.fulu.game.play.service.impl;

import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.enums.WechatTemplateIdEnum;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.common.enums.WechatTemplateMsgTypeEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.impl.push.MiniAppPushServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class PlayMiniAppPushServiceImpl extends MiniAppPushServiceImpl {


    @Override
    protected void push(int userId, WechatTemplateMsgEnum wechatTemplateMsgEnum, String... replaces) {
        pushWechatTemplateMsg(
                PlatformEcoEnum.PLAY.getType(),
                userId,
                WechatTemplateIdEnum.PLAY_LEAVE_MSG,
                wechatTemplateMsgEnum.getPage().getPlayPagePath(),
                wechatTemplateMsgEnum.getContent(),
                replaces);
    }

    @Override
    protected void push(int userId, Order order, WechatTemplateMsgEnum wechatTemplateMsgEnum,
                        WechatTemplateMsgTypeEnum wechatTemplateMsgTypeEnum, String... replaces) {
        pushServiceProcessMsg(
                PlatformEcoEnum.PLAY.getType(),
                userId,
                order,
                WechatTemplateIdEnum.PLAY_SERVICE_PROCESS_NOTICE,
                wechatTemplateMsgEnum,
                replaces);
    }


}
