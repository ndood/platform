package com.fulu.game.play.service.impl;

import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.enums.OrderTypeEnum;
import com.fulu.game.common.enums.WechatEcoEnum;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.WxTemplateMsgService;
import com.fulu.game.core.service.impl.push.MiniAppPushServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PlayMiniAppPushServiceImpl extends MiniAppPushServiceImpl {

    @Autowired
    private WxTemplateMsgService wxTemplateMsgService;

    @Override
    public void pushToServiceOrder(Order order, WechatTemplateMsgEnum wechatTemplateMsgEnum) {
        if (OrderTypeEnum.POINT.getType().equals(order.getType())) {
            if (order.getServiceUserId() != null) {
                wxTemplateMsgService.pushWechatTemplateMsg(order.getServiceUserId(), wechatTemplateMsgEnum.choice(WechatEcoEnum.POINT.getType()));
            }
        } else {
            wxTemplateMsgService.pushWechatTemplateMsg(order.getServiceUserId(), wechatTemplateMsgEnum);
        }
    }

    @Override
    public void pushToUserOrder(Order order, WechatTemplateMsgEnum wechatTemplateMsgEnum) {
        String orderStatus = OrderStatusEnum.getMsgByStatus(order.getStatus());
        if (OrderTypeEnum.POINT.getType().equals(order.getType())) {
            if (order.getServiceUserId() != null) {
                wxTemplateMsgService.pushWechatTemplateMsg(order.getUserId(), wechatTemplateMsgEnum.choice(WechatEcoEnum.POINT.getType()), orderStatus);
            }
        } else {
            wxTemplateMsgService.pushWechatTemplateMsg(order.getUserId(), wechatTemplateMsgEnum, orderStatus);
        }

    }
}
