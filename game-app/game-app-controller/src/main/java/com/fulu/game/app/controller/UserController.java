package com.fulu.game.app.controller;

import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.Result;
import com.fulu.game.common.domain.ClientInfo;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.AdminImLog;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.UserVO;
import com.fulu.game.core.service.AdminImLogService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private OssUtil ossUtil;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private AdminImLogService adminImLogService;


    /**
     * 修改/填写资料
     * @param userVO
     * @return
     */
    @RequestMapping("update")
    public Result update(UserVO userVO){
        User user = userService.findById(userService.getCurrentUser().getId());
        user.setAge(DateUtil.ageOfNow(userVO.getBirth()));
        user.setGender(userVO.getGender());
        user.setCity(userVO.getCity());
        user.setProvince(userVO.getProvince());
        user.setCountry(userVO.getCountry());
        user.setBirth(userVO.getBirth());
        user.setConstellation(userVO.getConstellation());
        user.setNickname(userVO.getNickname());
        user.setHeadPortraitsUrl(ossUtil.activateOssFile(userVO.getHeadPortraitsUrl()));
        userService.update(user);
        userService.updateRedisUser(user);
        user.setIdcard(null);
        user.setRealname(null);
        return Result.success().data(user).msg("个人信息设置成功！");
    }

    
    @PostMapping(value = "online")
    public Result userOnline(@RequestParam(required = true) Boolean active, String version){
        User user = userService.getCurrentUser();
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
