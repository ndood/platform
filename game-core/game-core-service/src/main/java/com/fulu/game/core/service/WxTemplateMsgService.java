package com.fulu.game.core.service;

import com.fulu.game.common.enums.WechatTemplateMsgEnum;

public interface WxTemplateMsgService {


     /**
      * 推送集市订单
      */
     void pushMarketOrder(String orderNo);

     /**
      * 通过枚举发送消息
      * @param userId
      * @param wechatTemplateMsgEnum
      * @param replaces
      * @return
      */
     void pushWechatTemplateMsg(int userId, WechatTemplateMsgEnum wechatTemplateMsgEnum, String... replaces);

     /**
      *
      * @param content
      * @param acceptImId 接收者IMid
      * @param imId  发送者IMid
      * @return
      */
     String pushIMWxTemplateMsg(String content,String acceptImId,String imId);

     /**
      * 管理员推送微信消息
      * @param pushId
      * @param userId
      * @param page
      * @param content
      */
     void adminPushWxTemplateMsg(int pushId, int userId, String  page, String  content);
}
