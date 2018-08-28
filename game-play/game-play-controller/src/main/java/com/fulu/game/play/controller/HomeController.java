package com.fulu.game.play.controller;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.fulu.game.common.Result;
import com.fulu.game.common.config.WxMaServiceSupply;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.exception.ParamsException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.Banner;
import com.fulu.game.core.entity.SysConfig;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.BannerVO;
import com.fulu.game.core.service.BannerService;
import com.fulu.game.core.service.SysConfigService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.play.shiro.PlayUserToken;
import com.fulu.game.play.utils.RequestUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class HomeController extends BaseController{

    @Autowired
    private WxMaServiceSupply wxMaServiceSupply;
    @Autowired
    private UserService userService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private BannerService bannerService;

    @PostMapping("/banner/list")
    @ResponseBody
    public Result list() {
        BannerVO bannerVO = new BannerVO();
        bannerVO.setDisable(true);
        List<Banner> bannerList = bannerService.findByParam(bannerVO);
        return Result.success().data(bannerList);
    }

    /**
     * 初始化加载系统配置
     * @return
     */
    @RequestMapping(value = "/sys/config", method = RequestMethod.POST)
    @ResponseBody
    public Result sysConfig(@RequestParam(value = "version", required = false) String version) {
        List<SysConfig> result = sysConfigService.findByVersion(version, PlatformEcoEnum.PLAY.getType());
        if (CollectionUtil.isEmpty(result)) {
            result = new ArrayList<SysConfig>();
            SysConfig sysConfig1 = new SysConfig();
            sysConfig1.setName("MMCON");
            sysConfig1.setValue("CLOSE");
            SysConfig sysConfig2 = new SysConfig();
            sysConfig2.setName("PAYCON");
            sysConfig2.setValue("CLOSE");
            result.add(sysConfig1);
            result.add(sysConfig2);
        }
        return Result.success().data(result);
    }

    /**
     * 小程序提交参数code
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Result login(@RequestParam("code") String code,
                        @RequestParam(value = "sourceId", required = false) Integer sourceId,
                        HttpServletRequest request) throws WxErrorException {
        if (StringUtils.isBlank(code)) {
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }
        WxMaJscode2SessionResult session = wxMaServiceSupply.playWxMaService().getUserService().getSessionInfo(code);
        String openId = session.getOpenid();
        //1.认证和凭据的token
        PlayUserToken playUserToken = new PlayUserToken(openId, session.getSessionKey(), sourceId);
        String ip = RequestUtil.getIpAdrress(request);
        playUserToken.setHost(ip);
        Subject subject = SecurityUtils.getSubject();
        //2.提交认证和凭据给身份验证系统
        try {
            subject.login(playUserToken);
            User user = userService.getCurrentUser();
            userService.updateUserIpAndLastTime(ip);
            user.setOpenId(null);
            user.setPointOpenId(null);
            user.setBalance(null);
            Map<String, Object> result = BeanUtil.beanToMap(user);
            result.put("token", SubjectUtil.getToken());
            result.put("userId", user.getId());
            if(user.getUnionId()==null){
                log.error("用户没有unionId;user{}",user);
                return Result.newUser().data(result);
            }
            return Result.success().data(result).msg("登录成功!");
        }
        catch (AuthenticationException e) {
            if(e.getCause() instanceof UserException){
                if(UserException.ExceptionCode.USER_BANNED_EXCEPTION.equals(((UserException) e.getCause()).getExceptionCode())){
                    log.error("用户被封禁,openId:{}", openId);
                    return Result.userBanned();
                }
            }
            return Result.noLogin().msg("测试登录用户验证信息错误！");
        }  catch (Exception e) {
            log.error("登录异常!", e);
            return Result.error().msg("登陆异常！");
        }
    }


    @RequestMapping(value = "/test/login", method = RequestMethod.POST)
    @ResponseBody
    public Result testLogin(String openId, @RequestParam(value = "sourceId", required = false) Integer sourceId, HttpServletRequest request) {
        log.info("==调用/test/login方法==");
        PlayUserToken playUserToken = new PlayUserToken(openId, "", sourceId);
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
        }
        catch (AuthenticationException e) {
            if(e.getCause() instanceof UserException){
                if(UserException.ExceptionCode.USER_BANNED_EXCEPTION.equals(((UserException) e.getCause()).getExceptionCode())){
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
