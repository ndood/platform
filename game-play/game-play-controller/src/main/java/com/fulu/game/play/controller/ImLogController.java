package com.fulu.game.play.controller;


import com.alibaba.fastjson.JSON;
import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.VirtualProductTypeEnum;
import com.fulu.game.core.entity.AdminImLog;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserInfoAuth;
import com.fulu.game.core.entity.VirtualProductAttach;
import com.fulu.game.core.entity.vo.AdminImLogVO;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.entity.vo.VirtualProductAttachVO;
import com.fulu.game.core.entity.vo.VirtualProductVO;
import com.fulu.game.core.entity.vo.searchVO.UserInfoAuthSearchVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/imlog")
@Slf4j
public class ImLogController extends BaseController{

    @Autowired
    private UserService userService;
    @Qualifier(value = "userInfoAuthServiceImpl")
    @Autowired
    private UserInfoAuthService userInfoAuthService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private AdminImLogService adminImLogService;
    @Autowired
    private VirtualProductService virtualProductService;
    @Autowired
    private VirtualProductAttachService virtualProductAttachService;

    @PostMapping(value = "collect")
    public Result log(String content){
        log.error("日志收集:{}",content);
        return Result.success();
    }


    @PostMapping(value = "online")
    public Result userOnline(@RequestParam(required = true) Boolean active, String version){
        User user = userService.getCurrentUser();
        UserInfoAuth ua = userInfoAuthService.findByUserId(user.getId());
        if(active){
            log.info("userId:{}用户上线了!;version:{}",user.getId(),version);
            redisOpenService.set(RedisKeyEnum.USER_ONLINE_KEY.generateKey(user.getId()),user.getType()+"");

            
            if(ua!=null && ua.getImSubstituteId()!=null){


                //删除陪玩师的未读信息数量
                Map map = redisOpenService.hget(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(ua.getImSubstituteId().intValue()));

                if(map != null && map.size() >0 ){
                    
                    map.remove(user.getImId());

                    if(map.size() <1){
                        redisOpenService.delete(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(ua.getImSubstituteId().intValue()));
                    }else{
                        redisOpenService.hset(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(ua.getImSubstituteId().intValue()) , map , Constant.ONE_DAY * 3);
                    }

                }

                //获取代聊天记录
                AdminImLogVO ail = new AdminImLogVO();
                ail.setOwnerUserId(user.getId());
                List<AdminImLog> list = adminImLogService.findByParameter(ail);
                //删除带聊天记录
                adminImLogService.deleteByOwnerUserId(user.getId());
                return Result.success().data(list).msg("查询成功！");
                
            }

            return Result.success().msg("查询成功！");

        }else{
            log.info("userId:{}用户下线了!version:{}",user.getId(),version);
            redisOpenService.delete(RedisKeyEnum.USER_ONLINE_KEY.generateKey(user.getId()));
        }
        return Result.success();
    }


    //增加陪玩师未读消息数量
    @RequestMapping("/send")
    public Result sendMessage(@RequestParam(value = "targetImId", required = false) String targetImId) {

        UserInfoAuthSearchVO uavo = new UserInfoAuthSearchVO();
        uavo.setImId(targetImId);
        List<UserInfoAuthVO> uaList = userInfoAuthService.findBySearchVO(uavo);

        UserInfoAuthVO targetUser = uaList.get(0);

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
                        targetUser.setUnreadCount(temp.getUnreadCount().longValue() + 1);
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


    /**
     * 解锁图片  声音  私照
     * @return
     */
    @PostMapping(value = "/unlock")
    public Result unlockPrivatePic(Integer virtualProductId) {
        User user = userService.getCurrentUser();
        VirtualProductVO vpo = new VirtualProductVO();
        vpo.setUserId(user.getId());
        vpo.setType(VirtualProductTypeEnum.PERSONAL_PICS.getType());
        vpo.setDelFlag(false);

        virtualProductService.unlockProduct(user.getId(),virtualProductId);

        return Result.success().msg("解锁成功");
        
    }


    /**
     * 查看解锁商品
     * @return
     */
    @PostMapping(value = "/unlock-product/list")
    public Result unlockProductList(Integer virtualProductId) {

        User user = userService.getCurrentUser();

        List<VirtualProductAttach> list = virtualProductAttachService.findByOrderProIdUserId(user.getId(),virtualProductId);

        return Result.success().data(list).msg("查询成功");
    }
    
}
