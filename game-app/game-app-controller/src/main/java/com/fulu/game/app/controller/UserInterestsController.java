package com.fulu.game.app.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.UserInterests;
import com.fulu.game.core.service.UserInterestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/8/31 17:44.
 * @Description: 用户兴趣
 */
@RestController
@RequestMapping("/api/v1/user-interests")
public class UserInterestsController  extends BaseController{

    @Autowired
    private UserInterestsService userInterestsService;

    @RequestMapping(value = "find-all")
    public Result searchUsers() {
        List<UserInterests> list = userInterestsService.findAll();
        return Result.success().data(list).msg("成功");
    }

}
