package com.fulu.game.h5.controller.fenqile;

import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.PlatformBannerEnum;
import com.fulu.game.common.exception.LoginException;
import com.fulu.game.common.exception.ParamsException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.Banner;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.BannerVO;
import com.fulu.game.core.service.BannerService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.h5.controller.BaseController;
import com.fulu.game.h5.shiro.PlayUserToken;
import com.fulu.game.h5.utils.RequestUtil;
import com.fulu.game.thirdparty.fenqile.entity.CodeSessionResult;
import com.fulu.game.thirdparty.fenqile.service.FenqileAuthService;
import com.fulu.game.thirdparty.fenqile.service.FenqileSdkOrderService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 分期乐-HomeController
 *
 * @author Gong ZeChun
 * @date 2018/8/13 15:48
 */
@Controller
@Slf4j
@RequestMapping("/fenqile")
public class FenqileHomeController extends BaseController {


    private final BannerService bannerService;

    private final UserService userService;

    private final FenqileAuthService fenqileAuthService;

    private final FenqileSdkOrderService fenqileSdkOrderService;

    @Autowired
    private RedisOpenServiceImpl redisOpenService;


    @Autowired
    public FenqileHomeController(BannerService bannerService,
                                 UserService userService,
                                 FenqileAuthService fenqileAuthService,
                                 FenqileSdkOrderService fenqileSdkOrderService) {
        this.bannerService = bannerService;
        this.userService = userService;
        this.fenqileAuthService = fenqileAuthService;
        this.fenqileSdkOrderService = fenqileSdkOrderService;
    }


    /**
     * banner展示列表
     *
     * @return 封装结果集
     */
    @PostMapping("/banner/list")
    @ResponseBody
    public Result list() {
        BannerVO bannerVO = new BannerVO();
        bannerVO.setDisable(true);
        bannerVO.setPlatformType(PlatformBannerEnum.PLAY.getType());
        List<Banner> bannerList = bannerService.findByParam(bannerVO);
        return Result.success().data(bannerList);
    }

    //todo 这个版本需要把订单平台属性加上
    @RequestMapping(value = "/login")
    @ResponseBody
    public Result login(@RequestParam("code") String code,
                        HttpServletRequest request) throws WxErrorException {
        if (StringUtils.isBlank(code)) {
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }
        log.info("分期乐code:{}", code);
        CodeSessionResult session = null;
        try {
            session = fenqileAuthService.accessToken(code);
            log.info("分期乐授权成功session:{}", session);
        } catch (Exception e) {
            log.error("分期乐授权错误", e);
            throw new LoginException(LoginException.ExceptionCode.FENQILE_AUTH_ERROR);
        }
        //1.认证和凭据的token
        PlayUserToken playUserToken = PlayUserToken.newBuilder(PlayUserToken.Platform.FENQILE)
                .fqlOpenid(session.getUid())
                .accessToken(session.getAccessToken())
                .build();

        String ip = RequestUtil.getIpAdrress(request);
        playUserToken.setHost(ip);
        Subject subject = SecurityUtils.getSubject();
        //2.提交认证和凭据给身份验证系统
        try {
            subject.login(playUserToken);
            User user = userService.getCurrentUser();
            userService.updateUserIpAndLastTime(ip);
            user.setBalance(null);
            Map<String, Object> result = BeanUtil.beanToMap(user);
            result.put("token", SubjectUtil.getToken());
            result.put("userId", user.getId());
            return Result.success().data(result).msg("登录成功!");
        } catch (AuthenticationException e) {
            if (e.getCause() instanceof UserException) {
                if (UserException.ExceptionCode.USER_BANNED_EXCEPTION.equals(((UserException) e.getCause()).getExceptionCode())) {
                    log.error("用户被封禁,openId:{}", session);
                    return Result.userBanned();
                }
            }
            return Result.noLogin();
        } catch (Exception e) {
            log.error("登录异常!", e);
            return Result.error().msg("登陆异常！");
        }
    }


    @RequestMapping(value = "/test/login")
    @ResponseBody
    public Result testLogin(String openId,
                            @RequestParam(value = "sourceId", required = false) Integer sourceId,
                            HttpServletRequest request) {
        log.info("==调用/test/login方法==");
        PlayUserToken playUserToken = PlayUserToken.newBuilder(PlayUserToken.Platform.FENQILE).fqlOpenid(openId).build();
        String ip = RequestUtil.getIpAdrress(request);
        playUserToken.setHost(ip);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(playUserToken);
            User user = userService.getCurrentUser();
            Map<String, Object> result = BeanUtil.beanToMap(user);
            result.put("token", SubjectUtil.getToken());
            result.put("userId", user.getId());
            return Result.success().data(result).msg("测试登录成功!");
        } catch (AuthenticationException e) {
            if (e.getCause() instanceof UserException) {
                if (UserException.ExceptionCode.USER_BANNED_EXCEPTION.equals(((UserException) e.getCause()).getExceptionCode())) {
                    log.error("用户被封禁,openId:{}", openId);
                    return Result.userBanned();
                }
            }
            return Result.noLogin().msg("测试登录用户验证信息错误！");
        } catch (Exception e) {
            log.error("测试登录异常!", e);
            return Result.error().msg("测试登陆异常！");
        }
    }

    /**
     * 更新分期乐回调url
     *
     * @return
     */
    @GetMapping(value = "url/setting")
    public Result settingUrl() {
        log.info("执行分期乐修改订单回调通知接口");
        fenqileSdkOrderService.modifyPlatformUrl();
        return Result.success();
    }
}
