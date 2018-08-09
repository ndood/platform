package com.fulu.game.core.service.impl.push;

import com.fulu.game.common.enums.WechatEcoEnum;
import com.fulu.game.common.enums.WechatTemplateIdEnum;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;

public class PointMiniAppPushServiceImpl extends MiniAppPushServiceImpl {


    @Override
    protected void push(int userId, WechatTemplateMsgEnum wechatTemplateMsgEnum) {
        pushWechatTemplateMsg(WechatEcoEnum.POINT.getType(), userId, WechatTemplateIdEnum.POINT_LEAVE_MSG, wechatTemplateMsgEnum.getPage().getPlayPagePath(), wechatTemplateMsgEnum.getContent());
    }

}
