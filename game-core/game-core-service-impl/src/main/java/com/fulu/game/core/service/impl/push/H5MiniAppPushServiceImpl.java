package com.fulu.game.core.service.impl.push;

import com.fulu.game.common.enums.WechatEcoEnum;
import com.fulu.game.common.enums.WechatTemplateIdEnum;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.core.entity.Order;
import org.springframework.stereotype.Service;

@Service
public class H5MiniAppPushServiceImpl extends MiniAppPushServiceImpl{


    @Override
    protected void push(int userId, WechatTemplateMsgEnum wechatTemplateMsgEnum,String ... replaces ) {
//        pushWechatTemplateMsg(WechatEcoEnum.PLAY.getType(),userId, WechatTemplateIdEnum.PLAY_LEAVE_MSG,wechatTemplateMsgEnum.getPage().getPlayPagePath(),wechatTemplateMsgEnum.getContent());
    }



    public void orderPay(Order order){
//        push(order.getServiceUserId(),WechatTemplateMsgEnum.ORDER_TOSERVICE_PAY);
    }
}
