package com.fulu.game.play.controller;


import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/imlog")
@Slf4j
public class ImLogController extends BaseController{

    @Autowired
    private UserService userService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    @PostMapping(value = "collect")
    public Result log(String content){
        log.error("日志收集:{}",content);
        return Result.success();
    }


    @PostMapping(value = "online")
    public Result userOnline(@RequestParam(required = true) Boolean active){
        User user = userService.getCurrentUser();
        if(active){
            log.info("userId:{}用户上线了!",user.getId());
            redisOpenService.set(RedisKeyEnum.USER_ONLINE_KEY.generateKey(user.getId()),user.getType()+"");
        }else{
            log.info("userId:{}用户下线了!",user.getId());
            redisOpenService.delete(RedisKeyEnum.USER_ONLINE_KEY.generateKey(user.getId()));
        }
        return Result.success();
    }

}
