package com.fulu.game.h5.controller.mp;

import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.config.WxMaServiceSupply;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.exception.LoginException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.SMSUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.h5.controller.BaseController;
import com.fulu.game.h5.shiro.PlayUserToken;
import com.fulu.game.h5.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 微信公众号Controller
 *
 * @author Gong ZeChun
 * @date 2018/9/3 15:54
 */
@Controller
@Slf4j
@RequestMapping("/api/v1/mp")
public class MpHomeController extends BaseController {

    @Autowired
    private WxMaServiceSupply wxMaServiceSupply;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private UserService userService;




    @PostMapping(value = "login")
    public Result login(@RequestParam(required = true) String code,
                        @RequestParam(required = true) String mobile,
                        @RequestParam(required = true, defaultValue = "") String verifyCode,
                        HttpServletRequest request) {
        String ip = RequestUtil.getIpAdrress(request);
        String redisVerifyCode = redisOpenService.get(RedisKeyEnum.SMS_VERIFY_CODE.generateKey(mobile));
        if (!verifyCode.equals(redisVerifyCode)) {
            throw new LoginException(LoginException.ExceptionCode.VERIFY_CODE_ERROR);
        }
        try {
            WxMpOAuth2AccessToken accessToken = wxMaServiceSupply.wxMpService().oauth2getAccessToken(code);
            WxMpUser wxMpUser = wxMaServiceSupply.wxMpService().getUserService().userInfo(accessToken.getOpenId(), "zh_CN");
            PlayUserToken playUserToken = PlayUserToken.newBuilder(PlayUserToken.Platform.MP)
                    .mpOpenId(wxMpUser.getOpenId())
                    .unionId(wxMpUser.getUnionId())
                    .mobile(mobile)
                    .host(ip)
                    .build();
            Subject subject = SecurityUtils.getSubject();
            subject.login(playUserToken);
            User user = userService.getCurrentUser();
            userService.updateUserIpAndLastTime(ip);
            Map<String, Object> result = BeanUtil.beanToMap(user);
            result.put("token", SubjectUtil.getToken());
            return Result.success().data(result).msg("登录成功!");
        } catch (WxErrorException e) {
            throw new LoginException(LoginException.ExceptionCode.WX_AUTH_ERROR);
        } catch (AuthenticationException e) {
            if (e.getCause() instanceof UserException) {
                if (UserException.ExceptionCode.USER_BANNED_EXCEPTION.equals(((UserException) e.getCause()).getExceptionCode())) {
                    log.error("用户被封禁,mobile:{}", mobile);
                    return Result.userBanned();
                }
            }
            return Result.error();
        } catch (Exception e) {
            log.error("登录异常!", e);
            return Result.error().msg("登陆异常！");
        }
    }


    /**
     * 点击发送验证码接口
     * @param mobile
     * @return
     */
    @PostMapping("/sms/verify")
    public Result sms(@RequestParam("mobile") String mobile) {
        //缓存中查找该手机是否有验证码
        if (redisOpenService.hasKey(RedisKeyEnum.SMS.generateKey(mobile))) {
            String times = redisOpenService.get(RedisKeyEnum.SMS.generateKey(mobile));
            if (Integer.parseInt(times) > Constant.MOBILE_CODE_SEND_TIMES) {
                return Result.error().msg("半小时内发送次数不能超过" + Constant.MOBILE_CODE_SEND_TIMES + "次，请等待！");
            } else {
                String verifyCode = SMSUtil.sendVerificationCode(mobile);
                log.info("发送验证码{}={}", mobile, verifyCode);
                redisOpenService.set(RedisKeyEnum.SMS_VERIFY_CODE.generateKey(mobile), verifyCode, Constant.VERIFYCODE_CACHE_TIME);
                times = String.valueOf(Integer.parseInt(times) + 1);
                redisOpenService.set(RedisKeyEnum.SMS_VERIFY_CODE_TIMES.generateKey(mobile), times, Constant.MOBILE_CACHE_TIME);
                return Result.success().data(verifyCode).msg("验证码发送成功！");
            }
        } else {
            String verifyCode = SMSUtil.sendVerificationCode(mobile);
            log.info("发送验证码{}={}", mobile, verifyCode);
            redisOpenService.set(RedisKeyEnum.SMS_VERIFY_CODE.generateKey(mobile), verifyCode, Constant.VERIFYCODE_CACHE_TIME);
            redisOpenService.set(RedisKeyEnum.SMS_VERIFY_CODE_TIMES.generateKey(mobile), "1", Constant.MOBILE_CACHE_TIME);
            return Result.success().data(verifyCode).msg("验证码发送成功！");
        }
    }


    @GetMapping(value = "/authurl")
    @ResponseBody
    public Result getAuthUrl(String redirectURI) {
        String url = wxMaServiceSupply.wxMpService().oauth2buildAuthorizationUrl(redirectURI, "snsapi_base", "");
        System.out.println(url);
        return Result.success().msg(url);
    }


}
