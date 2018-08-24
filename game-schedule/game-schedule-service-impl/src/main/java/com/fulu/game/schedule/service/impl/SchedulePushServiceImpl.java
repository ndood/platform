package com.fulu.game.schedule.service.impl;

import com.fulu.game.common.enums.OrderTypeEnum;
import com.fulu.game.common.enums.WechatEcoEnum;
import com.fulu.game.common.enums.WechatTemplateIdEnum;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.impl.push.PushServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SchedulePushServiceImpl extends PushServiceImpl {

    /**
     * 同意协商
     *
     * @param order
     */
    public void consultAgree(Order order) {
        if (OrderTypeEnum.PLATFORM.getType().equals(order.getType())) {
            pushServiceProcessMsg(WechatEcoEnum.PLAY.getType(),
                    order.getUserId(),
                    order,
                    WechatTemplateIdEnum.PLAY_SERVICE_PROCESS_NOTICE,
                    WechatTemplateMsgEnum.ORDER_TOUSER_CONSULT_AGREE);
        } else if (OrderTypeEnum.POINT.getType().equals(order.getType())) {
            pushServiceProcessMsg(WechatEcoEnum.POINT.getType(),
                    order.getUserId(),
                    order,
                    WechatTemplateIdEnum.POINT_SERVICE_PROCESS_NOTICE,
                    WechatTemplateMsgEnum.ORDER_TOUSER_CONSULT_AGREE);
        } else if (OrderTypeEnum.H5.getType().equals(order.getType())) {
            //todo 给用户发留言短信
        }
    }

    /**
     * 取消协商
     *
     * @param order
     */
    public void consultCancel(Order order) {
        if (OrderTypeEnum.PLATFORM.getType().equals(order.getType())) {
            pushServiceProcessMsg(WechatEcoEnum.PLAY.getType(),
                    order.getUserId(),
                    order,
                    WechatTemplateIdEnum.PLAY_SERVICE_PROCESS_NOTICE,
                    WechatTemplateMsgEnum.ORDER_TOSERVICE_CONSULT_CANCEL);
        } else if (OrderTypeEnum.POINT.getType().equals(order.getType())) {
            pushServiceProcessMsg(WechatEcoEnum.POINT.getType(),
                    order.getUserId(),
                    order,
                    WechatTemplateIdEnum.POINT_SERVICE_PROCESS_NOTICE,
                    WechatTemplateMsgEnum.ORDER_TOSERVICE_CONSULT_CANCEL);
        } else if (OrderTypeEnum.H5.getType().equals(order.getType())) {
            //todo 给用户发留言短信
        }
    }
}
