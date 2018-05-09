package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/msg")
public class MsgController {



    @RequestMapping("/wechat/send")
    public Result sendWechatMsg(String formId,
                                String imId){


        return Result.success();
    }


}
