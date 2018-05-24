package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthController extends BaseController{

    @Autowired
    private UserInfoAuthService userInfoAuthService;

    /**
     * 保存认证信息
     * @param userInfoAuthVO
     * @return
     */
    @PostMapping(value = "/info/save")
    public Result userAuthSave(UserInfoAuthVO userInfoAuthVO){
        userInfoAuthService.save(userInfoAuthVO);
        return Result.success().data(userInfoAuthVO);
    }

    /**
     * 认证信息查询
     * @param userId
     * @return
     */
    @PostMapping(value = "/info/query")
    public Result userAuthQuery(Integer userId){
        UserInfoAuthVO userInfoAuthVO =userInfoAuthService.findUserAuthInfoByUserId(userId);
        return Result.success().data(userInfoAuthVO);
    }







}
