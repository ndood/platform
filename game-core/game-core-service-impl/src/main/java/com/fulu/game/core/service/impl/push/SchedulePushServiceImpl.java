package com.fulu.game.core.service.impl.push;

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
     * @param order
     */
    public void consultAgree(Order order) {
        if (OrderTypeEnum.PLATFORM.getType().equals(order.getType())) {
            pushWechatTemplateMsg(WechatEcoEnum.PLAY.getType(), order.getUserId(), WechatTemplateIdEnum.PLAY_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_TOUSER_CONSULT_AGREE.getPage().getPlayPagePath(),WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_USER_WIN.getContent());
        } else if(OrderTypeEnum.POINT.getType().equals(order.getType())) {
            pushWechatTemplateMsg(WechatEcoEnum.POINT.getType(), order.getUserId(), WechatTemplateIdEnum.POINT_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_TOUSER_CONSULT_AGREE.getPage().getPointPagePath(),WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_USER_WIN.getContent());
        }else if(OrderTypeEnum.H5.getType().equals(order.getType())){
            //todo 给用户发留言短信
        }
    }

    /**
     * 取消协商
     * @param order
     */
    public void consultCancel(Order order) {
        if (OrderTypeEnum.PLATFORM.getType().equals(order.getType())) {
            pushWechatTemplateMsg(WechatEcoEnum.PLAY.getType(), order.getUserId(), WechatTemplateIdEnum.PLAY_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_TOSERVICE_CONSULT_CANCEL.getPage().getPlayPagePath(),WechatTemplateMsgEnum.ORDER_TOSERVICE_CONSULT_CANCEL.getContent());
        } else if(OrderTypeEnum.POINT.getType().equals(order.getType())){
            pushWechatTemplateMsg(WechatEcoEnum.POINT.getType(), order.getUserId(), WechatTemplateIdEnum.POINT_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_TOSERVICE_CONSULT_CANCEL.getPage().getPointPagePath(),WechatTemplateMsgEnum.ORDER_TOSERVICE_CONSULT_CANCEL.getContent());
        }else if(OrderTypeEnum.H5.getType().equals(order.getType())){
            //todo 给用户发留言短信
        }
    }

}
