package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.service.WxTemplateMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/msg")
public class WxTemplateController {


    @Autowired
    private WxTemplateMsgService wxTemplateMsgService;


    @RequestMapping("/push")
    public Result pushWechatMsg(String content,
                                String imId)throws Exception{
        log.info("推送模板消息imId:{},content:{}",imId,content);
        String result = wxTemplateMsgService.pushWechatTemplateMsg(content,imId);
        return Result.success().msg(result);
    }






}
