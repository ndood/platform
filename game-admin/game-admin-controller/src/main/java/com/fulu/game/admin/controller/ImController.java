package com.fulu.game.admin.controller;

import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.core.entity.vo.AdminImLogVO;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.service.AdminImLogService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map.Entry;

import java.util.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/im")
public class ImController extends BaseController {

    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    
    @Autowired
    private AdminImLogService adminImLogService;

    
    //减少未读消息数量
    @RequestMapping("/set-count")
    public Result update(@RequestParam(value = "imSubstituteId", required = false) Integer imSubstituteId,
                     @RequestParam(value = "imId", required = false) String imId,
                     @RequestParam(value = "actionCount", required = false) Integer actionCount){


        Map map = redisOpenService.hget(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(imSubstituteId.intValue()));
        
        if(map != null && map.size() >0){
            
            if(map.get(imId)!=null){

                UserInfoAuthVO temp = (UserInfoAuthVO)map.get(imId);
                
                long currentCount = temp.getUnreadCount() - actionCount.intValue();
                temp.setUnreadCount(currentCount);
                
                if(currentCount > 0){
                    map.put(imId, temp);
                }else{
                    map.remove(imId);
                }

                //更新未读消息数
                if(map.size() < 1){
                    redisOpenService.delete(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(imSubstituteId.intValue()));
                }else{
                    redisOpenService.hset(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(imSubstituteId.intValue()), map, Constant.ONE_DAY * 3);
                }
                
            }
            
            
        }

        return Result.success().msg("操作成功");
    }

    
    //获取未读消息陪玩师列表
    @RequestMapping("/list")
    public Result list(@RequestParam(value = "imSubstituteId", required = false) Integer imSubstituteId){


        Map map = redisOpenService.hget(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(imSubstituteId.intValue()));

        List list = new ArrayList();
        if(map!=null && map.size()>0){
            Iterator iter = map.entrySet().iterator(); 
            while (iter.hasNext()) {
                Entry entry = (Entry) iter.next();
                list.add(entry.getValue());
            }
        }
        

        return Result.success().data(list).msg("操作成功");
    }

    //保存admin上的im聊天信息
    @RequestMapping("/save-log")
    public Result save(AdminImLogVO adminImLogVO) {


        adminImLogService.create(adminImLogVO);

        return Result.success().msg("操作成功");
    }
        
}
