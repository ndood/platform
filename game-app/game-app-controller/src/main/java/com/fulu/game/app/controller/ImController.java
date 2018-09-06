package com.fulu.game.app.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserInfoAuth;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.entity.vo.UserVO;
import com.fulu.game.core.entity.vo.searchVO.UserInfoAuthSearchVO;
import com.fulu.game.core.service.UserInfoAuthService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/im")
public class ImController extends BaseController {

    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    @Qualifier(value = "userInfoAuthServiceImpl")
    @Autowired
    private UserInfoAuthService userInfoAuthService;
    
    //增加陪玩师未读消息数量
    @RequestMapping("/send")
    public Result sendMessage(@RequestParam(value = "targetImId", required = false) String targetImId) {

        UserInfoAuthSearchVO uavo = new UserInfoAuthSearchVO();
        uavo.setImId(targetImId);
        List<UserInfoAuthVO> uaList = userInfoAuthService.findBySearchVO(uavo);

        UserInfoAuthVO targetUser = new UserInfoAuthVO();
        if(uaList!=null && uaList.size() > 0){
            targetUser = uaList.get(0);
        }

        //判断im目标用户是否为代聊用户
        if (targetUser.getImSubstituteId() != null) {

            //判断目标用户是否在线
            String onlineStatus = redisOpenService.get(RedisKeyEnum.USER_ONLINE_KEY.generateKey(targetUser.getId()));

            if (StringUtils.isNotBlank(onlineStatus)) {
                //删除目标用户的未读信息
                redisOpenService.delete(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(targetUser.getImSubstituteId().intValue()));

            } else {
                //增加未读消息数量+1
                Map map = redisOpenService.hget(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(targetUser.getImSubstituteId().intValue()));


                if(map == null || map.size() == 0){
                    map = new HashMap();
                    targetUser.setUnreadCount(new Long(1));
                }else{
                    if(map.get(targetImId)!=null){

                        UserInfoAuthVO temp = JSON.parseObject(map.get(targetImId).toString(),UserInfoAuthVO.class);
                        targetUser.setUnreadCount(temp.getUnreadCount() + 1);
                    }else{
                        targetUser.setUnreadCount(new Long(1));
                    }
                }
                map.put(targetImId, JSON.toJSONString(targetUser));
                //更新未读消息数
                redisOpenService.hset(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(targetUser.getImSubstituteId().intValue()), map, Constant.ONE_DAY * 3);
            }

        }

        return Result.success().msg("操作成功");

    }

}
