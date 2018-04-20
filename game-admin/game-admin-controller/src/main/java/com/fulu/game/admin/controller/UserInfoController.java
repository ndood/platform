package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.service.UserInfoAuthService;
import com.fulu.game.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/userinfo")
public class UserInfoController extends BaseController{

    @Autowired
    private UserInfoAuthService userInfoAuthService;
    @Autowired
    private UserService userService;
    /**
     * 认证信息创建
     * @return
     */
    @PostMapping(value = "/auth/create")
    public Result userInfoAuthCreate(UserInfoAuthVO userInfoAuthVO){
        userInfoAuthService.create(userInfoAuthVO);
        return Result.success().data(userInfoAuthVO);
    }

    /**
     * 查询用户个人认证信息
     * @param userId
     * @return
     */
    @PostMapping(value = "/auth/info")
    public Result userAuthInfo(Integer userId){
        UserInfoAuthVO userInfoAuthVO =userInfoAuthService.findUserAuthInfoByUserId(userId);
        return Result.success().data(userInfoAuthVO);
    }

    /**
     * 通过用户手机查询用户信息
     * @param mobile
     * @return
     */
    @PostMapping(value = "/mobile")
    public Result findByMobile(String mobile){
        User user = userService.findByMobile(mobile);
        return Result.success().data(user);
    }


}
