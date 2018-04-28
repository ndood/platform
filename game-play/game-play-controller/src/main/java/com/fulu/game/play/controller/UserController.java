package com.fulu.game.play.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.utils.SMSUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.WxUserInfo;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.UserTechAuthService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController extends BaseController{

    private static final String SPLIT = "-";

    @Autowired
    private UserTechAuthService userTechAuthService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    @RequestMapping("tech/list")
    public Result userTechList(){
        User user =(User) SubjectUtil.getCurrentUser();
        //查询所有用户认证的技能
        List<UserTechAuth> techAuthList = userTechAuthService.findByUserId(user.getId(),true);
        return Result.success().data(techAuthList);
    }

    /**
     * 用户-查询余额
     * @return
     */
    @PostMapping("/balance/get")
    public Result getBalance(){
        String token = SubjectUtil.getToken();
        String openId = redisOpenService.hget(RedisKeyEnum.PLAY_TOKEN.generateKey(token)).get("openId").toString();
        User user = userService.findByOpenId(openId);
        return Result.success().data(user.getBalance()).msg("查询成功！");
    }

    /**
     * 点击发送验证码接口
     * @param mobile
     * @return
     */
    @PostMapping("/mobile/sms")
    public Result sms(@RequestParam("mobile") String mobile){
        String token = SubjectUtil.getToken();
        //缓存中查找该手机是否有验证码
        if (redisOpenService.hasKey(RedisKeyEnum.SMS.generateKey(mobile))){
            String times = redisOpenService.get(RedisKeyEnum.SMS.generateKey(mobile));
            if (Integer.parseInt(times) > 2){
                return Result.error().msg("半小时内发送次数不能超过3次，请等待！");
            }else{
                String verifyCode = SMSUtil.sendVerificationCode(mobile);
                redisOpenService.set(RedisKeyEnum.SMS.generateKey(token+SPLIT+mobile),verifyCode,60);
                times = String.valueOf(Integer.parseInt(times)+1);
                redisOpenService.set(RedisKeyEnum.SMS.generateKey(mobile),times,30*60);
                return Result.success().msg("验证码发送成功！");
            }
        }else{
            String verifyCode = SMSUtil.sendVerificationCode(mobile);
            log.info("验证码：" + verifyCode);
            redisOpenService.set(RedisKeyEnum.SMS.generateKey(token+SPLIT+mobile),verifyCode,60);
            redisOpenService.set(RedisKeyEnum.SMS.generateKey(mobile),"1",30*60);
            return Result.success().msg("验证码发送成功！");
        }
    }

    @PostMapping("/mobile/bind")
    public Result bind(@ModelAttribute WxUserInfo wxUserInfo, @RequestParam("verifyCode") String verifyCode){
        String token = SubjectUtil.getToken();
        log.info("获取到的token===========" + token);
        //验证手机号的验证码
        String redisVerifyCode = redisOpenService.get(RedisKeyEnum.SMS.generateKey(token+SPLIT+wxUserInfo.getMobile()));
        log.info("前端验证码：" + verifyCode);

        log.info("缓存验证码：" + redisVerifyCode);
        if (null == redisVerifyCode){
            return Result.error().msg("验证码失效");
        }else{
            if (!verifyCode.equals(redisVerifyCode)){
                return Result.error().msg("验证码提交错误");
            }else{//绑定手机号
                String openId = redisOpenService.hget(RedisKeyEnum.PLAY_TOKEN.generateKey(token)).get("openId").toString();
                User user = userService.findByOpenId(openId);
                user.setMobile(wxUserInfo.getMobile());
                user.setGender(Integer.parseInt(wxUserInfo.getGender()));
                user.setNickname(wxUserInfo.getNickName());
                user.setHeadPortraitsUrl(wxUserInfo.getAvatarUrl());
                user.setCity(wxUserInfo.getCity());
                user.setProvince(wxUserInfo.getProvince());
                user.setCountry(wxUserInfo.getCountry());
                user.setUpdateTime(new Date());
                userService.update(user);
                //如果是后台添加的用户，绑定后需要删除该记录
                User oldUser = userService.findByMobile(wxUserInfo.getMobile());
                if (null != oldUser){
                    userService.deleteById(oldUser.getId());
                }
                user.setOpenId(null);
                user.setSessionKey(null);
                user.setBalance(null);
                return Result.success().data(user).msg("手机号绑定成功！");
            }
        }
    }

}
