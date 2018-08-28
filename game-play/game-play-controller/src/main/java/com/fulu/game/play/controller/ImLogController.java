package com.fulu.game.play.controller;


import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.core.entity.AdminImLog;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.AdminImLogService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/imlog")
@Slf4j
public class ImLogController extends BaseController{

    @Autowired
    private UserService userService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private AdminImLogService adminImLogService;

    @PostMapping(value = "collect")
    public Result log(String content){
        log.error("日志收集:{}",content);
        return Result.success();
    }


    @PostMapping(value = "online")
    public Result userOnline(@RequestParam(required = true) Boolean active,String version){
//        User user = userService.getCurrentUser();
        User user = new User();
        user.setId(2102);
        user.setImSubstituteId(1);
        if(active){
            log.info("userId:{}用户上线了!;version:{}",user.getId(),version);
            redisOpenService.set(RedisKeyEnum.USER_ONLINE_KEY.generateKey(user.getId()),user.getType()+"");

            //删除陪玩师的未读信息数量
            if(user.getImSubstituteId()!=null){
                redisOpenService.delete(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(user.getImSubstituteId().intValue()));
            }

            //获取代聊天记录
            List<AdminImLog> list = adminImLogService.findByImId(user.getImId());
            //删除带聊天记录
            adminImLogService.deleteByImId(user.getImId());
            return Result.success().data(list).msg("查询成功！");
            
        }else{
            log.info("userId:{}用户下线了!version:{}",user.getId(),version);
            redisOpenService.delete(RedisKeyEnum.USER_ONLINE_KEY.generateKey(user.getId()));
        }
        return Result.success();
    }

}
