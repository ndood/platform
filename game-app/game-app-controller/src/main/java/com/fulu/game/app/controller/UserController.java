package com.fulu.game.app.controller;

import com.fulu.game.common.domain.ClientInfo;
import com.fulu.game.common.utils.SubjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController extends BaseController {


    @RequestMapping("test")
    public void test(){
        ClientInfo clientInfo = SubjectUtil.getUserClientInfo();
        System.out.println(clientInfo);
    }


}
