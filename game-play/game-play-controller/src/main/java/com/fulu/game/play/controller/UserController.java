package com.fulu.game.play.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.exception.ParamsExceptionEnums;
import com.fulu.game.common.utils.SMSUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.WxUserInfo;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.UserTechAuthService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.play.config.PlayProperties;
import com.fulu.game.play.controller.exception.ParamsException;
import com.fulu.game.play.shiro.PlayUserToken;
import com.xiaoleilu.hutool.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController extends BaseController{

    @Autowired
    private UserTechAuthService userTechAuthService;
    @Autowired
    private UserService userService;
    @Autowired
    private WxMaService wxService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    @RequestMapping("tech/list")
    public Result userTechList(){
        //查询所有用户认证的技能
        List<UserTechAuth> techAuthList = userTechAuthService.findByUserId(Constant.DEF_USER_ID,true);
        return Result.success().data(techAuthList);
    }

    /**
     * 用户-查询余额
     * @return
     */
    @PostMapping("/balance/get")
    public Result getBalance(){
        String token = SubjectUtil.getToken();
        String openId = redisOpenService.hget(token).get("openId").toString();
        User user = userService.findByOpenId(openId);
        return Result.success().data(user.getBalance()).msg("查询成功！");
    }

    /**
     * 小程序提交参数code
     * @return
     */
    @PostMapping(value = "/login")
    public Result login(@RequestParam("code") String code) throws WxErrorException {
        if (StringUtils.isBlank(code)) {
            throw new ParamsException(ParamsExceptionEnums.PARAM_NULL_EXCEPTION);
        }
        WxMaInMemoryConfig wxconfig = new WxMaInMemoryConfig();
        PlayProperties p = new PlayProperties();
        wxconfig.setAppid(p.getWechat().getAppId());
        wxconfig.setSecret(p.getWechat().getSecret());
        WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(code);
        String sessionKey = session.getSessionKey();
        String openId = session.getOpenid();

        //1.认证和凭据的token
        PlayUserToken playUserToken = new PlayUserToken(openId, sessionKey);
        Subject subject = SecurityUtils.getSubject();
        //2.提交认证和凭据给身份验证系统
        try{
            subject.login(playUserToken);
            return Result.success().data(SubjectUtil.getToken()).msg("登录成功!");
        }catch (AuthenticationException e) {
            return Result.error().msg("用户验证信息错误！");
        }catch (Exception e){
            log.error("登录异常!",e);
            return Result.error().msg("登陆异常！");
        }
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
        if (redisOpenService.hasKey(mobile)){
            String times = redisOpenService.get(mobile);
            if (Integer.parseInt(times) > 2){
                return Result.error().msg("半小时内发送次数不能超过3次，请等待！");
            }else{
                String verifyCode = SMSUtil.sendVerificationCode(mobile);
                redisOpenService.set(token+"_"+mobile,verifyCode,60);
                times = String.valueOf(Integer.parseInt(times)+1);
                redisOpenService.set(mobile,times,30*60);
                return Result.success().msg("验证码发送成功！");
            }
        }else{
            String verifyCode = SMSUtil.sendVerificationCode(mobile);
            redisOpenService.set(token+"_"+mobile,verifyCode,60);
            redisOpenService.set(mobile,"1",30*60);
            return Result.success().msg("验证码发送成功！");
        }
    }

    @PostMapping("/mobile/bind")
    public Result bind(@ModelAttribute WxUserInfo wxUserInfo, @RequestParam("verifyCode") String verifyCode){
        String token = SubjectUtil.getToken();
        //验证手机号的验证码
        String redisVerifyCode = redisOpenService.get(token + wxUserInfo.getMobile());
        if (null == redisVerifyCode){
            return Result.error().msg("验证码失效");
        }else{
            if (!verifyCode.equals(redisVerifyCode)){
                return Result.error().msg("验证码提交错误");
            }else{//绑定手机号
                String openId = redisOpenService.hget(token).get("openId").toString();
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

                user.setOpenId(null);
                user.setSessionKey(null);
                user.setBalance(null);
                return Result.success().data(user).msg("手机号绑定成功！");
            }
        }
    }

}
