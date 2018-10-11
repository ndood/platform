package com.fulu.game.h5.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.service.impl.push.PlayMiniAppPushServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/wxtemplate")
public class WxTemplateController extends BaseController {


    @Autowired
    private PlayMiniAppPushServiceImpl playMiniAppPushService;


    @RequestMapping("/push")
    public Result pushWechatMsg(String content,
                                String acceptImId,
                                String imId) throws Exception {
        log.info("推送模板消息imId:{},acceptImId:{},content:{}", imId, acceptImId, content);
        String result = playMiniAppPushService.pushIMWxTemplateMsg(content, acceptImId, imId);
        return Result.success().msg(result);
    }


}
