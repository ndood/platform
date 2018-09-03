package com.fulu.game.admin.controller;

import com.alibaba.fastjson.JSON;
import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.entity.Product;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualProduct;
import com.fulu.game.core.entity.vo.AdminImLogVO;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.entity.vo.UserInfoVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Autowired
    private UserService userService;
    
    @Autowired
    private VirtualProductService virtualProductService;

    
    //减少未读消息数量
    @RequestMapping("/set-count")
    public Result update(@RequestParam(value = "imSubstituteId", required = false) Integer imSubstituteId,
                     @RequestParam(value = "imId", required = false) String imId,
                     @RequestParam(value = "actionCount", required = false) Integer actionCount){


        Map map = redisOpenService.hget(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(imSubstituteId.intValue()));
        
        if(map != null && map.size() >0){
            
            if(map.get(imId)!=null){

                UserInfoAuthVO temp = JSON.parseObject(map.get(imId).toString(),UserInfoAuthVO.class);

                long currentCount = temp.getUnreadCount().longValue() - actionCount.longValue();
                temp.setUnreadCount(currentCount);
                
                if(currentCount > 0){
                    map.put(imId, JSON.toJSONString(temp));
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


    @PostMapping(value = "collect")
    public Result log(String content){
        log.error("日志收集:{}",content);
        return Result.success();
    }

    /**
     * 用户-根据imId查询聊天对象User
     *
     * @return
     */
    @PostMapping("/im/get")
    public Result getImUser(@RequestParam("imId") String imId,
                            String content) {
        log.info("根据imId获取用户信息:imId:{};content:{}", imId, content);
        if (StringUtils.isEmpty(imId)) {
            throw new UserException(UserException.ExceptionCode.IllEGAL_IMID_EXCEPTION);
        }
        User user = userService.findByImId(imId);
        if (null == user) {
            return Result.error().msg("未查询到该用户或尚未注册IM");
        }
        log.info("根据imId获取用户信息:imId:{};content:{};user:{}", imId, content, user);
        return Result.success().data(user).msg("查询IM用户成功");
    }

    //发送解锁
    @RequestMapping("/send-lock")
    public Result sendLock(Integer userId ,String name, Integer price, Integer type, Integer sort, String[] urls){

        VirtualProduct vp = new VirtualProduct();
        vp.setName(name);
        vp.setPrice(price);
        vp.setType(type);
        vp.setSort(sort);
        if(urls == null){
            vp.setAttachCount(0);
        }else{
            vp.setAttachCount(urls.length);
        }
        
        virtualProductService.createVirtualProduct(vp,userId,urls);

        return Result.success().msg("操作成功");
    }
}
