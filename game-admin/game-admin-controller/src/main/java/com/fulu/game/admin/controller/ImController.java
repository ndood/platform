package com.fulu.game.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.core.entity.AdminImLog;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.AdminImLogVO;
import com.fulu.game.core.service.AdminImLogService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/im")
public class ImController extends BaseController {

    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    
    @Autowired
    private AdminImLogService adminImLogService;

    
    //减少未读消息数量
    @RequestMapping("/add-count")
    public Result update(@RequestParam(value = "imSubstituteId", required = false) Integer imSubstituteId,
                     @RequestParam(value = "imId", required = false) String imId,
                     @RequestParam(value = "actionCount", required = false) Integer actionCount){


        Map map = redisOpenService.hget(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(imSubstituteId.intValue()));
        
        long currentCount = Long.parseLong(map.get(imId).toString()) - actionCount.intValue();
        if(currentCount > 0){
            map.put(imId, currentCount);
        }else{
            map.remove(imId);
        }
        
        //更新未读消息数
        redisOpenService.hset(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(imSubstituteId.intValue()), map, Constant.ONE_DAY * 3);

        return Result.success();
    }


    //保存admin上的im聊天信息
    @RequestMapping("/save-log")
    public Result save(AdminImLogVO AdminImLogVO) {


        adminImLogService.create(AdminImLogVO);

        return Result.success();
    }
        
}
