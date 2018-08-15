package com.fulu.game.h5.controller.fenqile;

import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.common.Result;
import com.fulu.game.common.exception.ParamsException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.Banner;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.BannerVO;
import com.fulu.game.core.service.BannerService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.h5.shiro.PlayUserToken;
import com.fulu.game.h5.utils.RequestUtil;
import com.fulu.game.thirdparty.fenqile.entity.CodeSessionResult;
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
public class HomeController extends BaseController {


    private final BannerService bannerService;

    private final UserService userService;




    @Autowired
    public HomeController(BannerService bannerService,
                          UserService userService) {
        this.bannerService = bannerService;
        this.userService = userService;
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
        List<Banner> bannerList = bannerService.findByParam(bannerVO);
        return Result.success().data(bannerList);
    }


    @RequestMapping(value = "/fenqile/login", method = RequestMethod.POST)
    @ResponseBody
    public Result login(@RequestParam("code") String code,
                        @RequestParam(value = "sourceId", required = false) Integer sourceId,
                        HttpServletRequest request) throws WxErrorException {
        if (StringUtils.isBlank(code)) {
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }
        CodeSessionResult session = new CodeSessionResult();
        session.setUid(code);
        session.setAccessToken("tempaccesstoken");
        String openId = session.getUid();
        //1.认证和凭据的token
        PlayUserToken playUserToken = new PlayUserToken(openId, session.getAccessToken());
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
        }
        catch (AuthenticationException e) {
            if(e.getCause() instanceof UserException){
                if(UserException.ExceptionCode.USER_BANNED_EXCEPTION.equals(((UserException) e.getCause()).getExceptionCode())){
                    log.error("用户被封禁,openId:{}", openId);
                    return Result.userBanned();
                }
            }
            return Result.noLogin();
        }  catch (Exception e) {
            log.error("登录异常!", e);
            return Result.error().msg("登陆异常！");
        }
    }


    @RequestMapping(value = "/fenqile/test/login", method = RequestMethod.POST)
    @ResponseBody
    public Result testLogin(String openId,
                            @RequestParam(value = "sourceId", required = false) Integer sourceId,
                            HttpServletRequest request) {
        log.info("==调用/test/login方法==");
        PlayUserToken playUserToken = new PlayUserToken(openId);
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


}
