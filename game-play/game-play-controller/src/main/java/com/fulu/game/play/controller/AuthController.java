package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.User;
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
    @Autowired
    private UserService userService;

    /**
     * 保存认证信息
     * @param userInfoAuthVO
     * @return
     */
    @PostMapping(value = "/info/save")
    public Result userAuthSave(UserInfoAuthVO userInfoAuthVO){
        userService.isCurrentUser(userInfoAuthVO.getUserId());
        //todo 判断用户是否是冻结和封禁状态
        userInfoAuthService.save(userInfoAuthVO);
        return Result.success().data(userInfoAuthVO);
    }


    /**
     * 认证信息查询
     * @return
     */
    @PostMapping(value = "/info/query")
    public Result userAuthQuery(){
        User user = userService.getCurrentUser();
        //todo 判断用户是否是冻结和封禁状态
        UserInfoAuthVO userInfoAuthVO =userInfoAuthService.findUserAuthInfoByUserId(user.getId());
        return Result.success().data(userInfoAuthVO);
    }


    @PostMapping(value = "/info/status")
    public Result userAuthStauts(){
        User user = userService.getCurrentUser();

        return Result.success();
    }





}
