package com.fulu.game.core.service;

import com.fulu.game.common.enums.WechatTemplateMsgEnum;

public interface WxTemplateMsgService {


     /**
      * 通过枚举发送消息
      * @param userId
      * @param wechatTemplateMsgEnum
      * @param replaces
      * @return
      */
     Boolean pushWechatTemplateMsg(Integer userId, WechatTemplateMsgEnum wechatTemplateMsgEnum, String... replaces);

     /**
      *
      * @param content
      * @param acceptImId 接收者IMid
      * @param imId  发送者IMid
      * @return
      */
     String pushWechatTemplateMsg(String content,String acceptImId,String imId);
}
