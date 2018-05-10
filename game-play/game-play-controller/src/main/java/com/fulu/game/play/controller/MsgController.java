package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/msg")
public class MsgController {

    @Autowired
    private UserService userService;

    @RequestMapping("/wechat/send")
    public Result sendWechatMsg(String formId,
                                String imId){
        userService.findByImId(imId);


        System.out.println(formId);
        System.out.println(imId);
        return Result.success();
    }




}
