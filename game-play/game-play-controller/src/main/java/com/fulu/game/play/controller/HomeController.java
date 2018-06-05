package com.fulu.game.play.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.fulu.game.common.Result;
import com.fulu.game.common.exception.ParamsException;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.Banner;
import com.fulu.game.core.entity.SysConfig;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.BannerVO;
import com.fulu.game.core.service.BannerService;
import com.fulu.game.core.service.SysConfigService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.play.shiro.PlayUserToken;
import com.xiaoleilu.hutool.util.CollectionUtil;
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

import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
public class HomeController {

    @Autowired
    private WxMaService wxService;
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
     *
     * @return
     */
    @RequestMapping(value = "/sys/config", method = RequestMethod.POST)
    @ResponseBody
    public Result sysConfig(@RequestParam(value = "version", required = false, defaultValue = "1.0.2") String version) {
        List<SysConfig> result = sysConfigService.findByVersion(version);
        if (CollectionUtil.isEmpty(result)){
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
     *
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Result login(@RequestParam("code") String code) throws WxErrorException {
        if (StringUtils.isBlank(code)) {
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }
        WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(code);
        String openId = session.getOpenid();
        log.info("调用/login接口，获取微信openId= {}", openId);
        //1.认证和凭据的token
        PlayUserToken playUserToken = new PlayUserToken(openId);
        Subject subject = SecurityUtils.getSubject();
        //2.提交认证和凭据给身份验证系统
        try {
            subject.login(playUserToken);
            User cachedUser = userService.getCurrentUser();
            User user = userService.findById(cachedUser.getId());
            JSONObject jo = new JSONObject();
            jo.put("token", SubjectUtil.getToken());
            jo.put("type", user.getType());
            if (StringUtils.isEmpty(user.getMobile())) {
                return Result.newUser().data(jo).msg("登录成功，请绑定手机号！");
            } else {
                return Result.success().data(jo).msg("登录成功!");
            }
        } catch (AuthenticationException e) {
            return Result.noLogin().msg("用户验证信息错误！");
        } catch (Exception e) {
            log.error("登录异常!", e);
            return Result.error().msg("登陆异常！");
        }
    }

    @RequestMapping(value = "/test/login", method = RequestMethod.POST)
    @ResponseBody
    public Result testLogin(String openId) {
        log.info("==调用/test/login方法==");
        PlayUserToken playUserToken = new PlayUserToken(openId);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(playUserToken);
            User cachedUser = (User) SubjectUtil.getCurrentUser();
            User user = userService.findById(cachedUser.getId());
            JSONObject jo = new JSONObject();
            jo.put("token", SubjectUtil.getToken());
            jo.put("type", user.getType());
            if (StringUtils.isEmpty(user.getMobile())) {
                return Result.newUser().data(jo).msg("测试登录成功，请绑定手机号！");
            } else {
                return Result.success().data(jo).msg("测试登录成功!");
            }
        } catch (AuthenticationException e) {
            return Result.noLogin().msg("测试登录用户验证信息错误！");
        } catch (Exception e) {
            log.error("测试登录异常!", e);
            return Result.error().msg("测试登陆异常！");
        }
    }


}
