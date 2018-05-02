package com.fulu.game.play.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.exception.ParamsExceptionEnums;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.User;
import com.fulu.game.play.config.PlayProperties;
import com.fulu.game.play.controller.exception.ParamsException;
import com.fulu.game.play.shiro.PlayUserToken;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
public class HomeController {

    @Autowired
    private WxMaService wxService;

    /**
     * 小程序提交参数code
     * @return
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
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
            User user = (User)SubjectUtil.getCurrentUser();
            JSONObject jo = new JSONObject();
            jo.put("token",SubjectUtil.getToken());
            if (StringUtils.isEmpty(user.getMobile())){
                return Result.newUser().data(jo).msg("登录成功，请绑定手机号！");
            }else{
                return Result.success().data(jo).msg("登录成功!");
            }
        }catch (AuthenticationException e) {
            return Result.noLogin().msg("用户验证信息错误！");
        }catch (Exception e){
            log.error("登录异常!",e);
            return Result.error().msg("登陆异常！");
        }
    }

    @RequestMapping(value = "/test/login",method = RequestMethod.POST)
    @ResponseBody
    public Result testLogin(String openId){
        String sessionKey = "xxxx";
        //1.认证和凭据的token
        PlayUserToken playUserToken = new PlayUserToken(openId, sessionKey);
        Subject subject = SecurityUtils.getSubject();
        //2.提交认证和凭据给身份验证系统
        try{
            subject.login(playUserToken);
            User user = (User)SubjectUtil.getCurrentUser();
            JSONObject jo = new JSONObject();
            jo.put("token",SubjectUtil.getToken());
            if (StringUtils.isEmpty(user.getMobile())){
                return Result.newUser().data(jo).msg("登录成功，请绑定手机号！");
            }else{
                return Result.success().data(jo).msg("登录成功!");
            }
        }catch (AuthenticationException e) {
            return Result.noLogin().msg("用户验证信息错误！");
        }catch (Exception e){
            log.error("登录异常!",e);
            return Result.error().msg("登陆异常！");
        }
    }

}
