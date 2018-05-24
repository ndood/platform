package com.fulu.game.core.service;

public interface WxTemplateMsgService {


     /**
      *
      * @param content
      * @param acceptImId 接收者IMid
      * @param imId  发送者IMid
      * @return
      */
     String pushWechatTemplateMsg(String content,String acceptImId,String imId);
}
