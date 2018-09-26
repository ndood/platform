package com.fulu.game.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.config.WxMaServiceSupply;
import com.fulu.game.common.config.WxMpServiceSupply;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.SysRole;
import com.fulu.game.core.entity.SysRouter;
import com.fulu.game.core.service.SysRoleService;
import com.fulu.game.core.service.SysRouterService;
import com.github.binarywang.wxpay.bean.entpay.EntPayRequest;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class HomeController {

    @Autowired
    private WxMpServiceSupply wxMpServiceSupply;
    @Autowired
    private WxMaServiceSupply wxMaServiceSupply;

    @Autowired
    private SysRouterService sysRouterService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Result login(String username,
                        String password,
                        @RequestParam(value = "rememberMe", required = false, defaultValue = "false") Boolean rememberMe) throws Exception {
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);
            Admin admin = (Admin) subject.getPrincipal();
            Map<String, Object> map = BeanUtil.beanToMap(admin);
            map.put("token", SubjectUtil.getToken());
            //获取用户菜单
            List<SysRouter> routerList = sysRouterService.findSysRouterListByAdminId(admin.getId());
            map.put("routes",routerList);
            return Result.success().data(map).msg("登录成功!");
        } catch (AuthenticationException e) {
            log.error("shiro验证失败", e);
            return Result.error().msg("登录失败,请联系管理员");
        } catch (Exception e) {
            log.error("登录异常", e);
            return Result.error().msg("登录异常");
        }
    }
}