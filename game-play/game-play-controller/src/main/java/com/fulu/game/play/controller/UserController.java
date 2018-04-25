package com.fulu.game.play.controller;

import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.service.UserTechAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController extends BaseController{

    @Autowired
    private UserTechAuthService userTechAuthService;


    @RequestMapping("tech/list")
    public Result userTechList(){
        //查询所有用户认证的技能
        List<UserTechAuth> techAuthList = userTechAuthService.findByUserId(Constant.DEF_USER_ID,true);
        return Result.success().data(techAuthList);
    }




}
