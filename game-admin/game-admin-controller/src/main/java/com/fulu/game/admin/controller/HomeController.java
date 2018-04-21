package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
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

@Controller
@Slf4j
public class HomeController {


    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String index(){
        return "index";
    }


    @RequestMapping(value = "/nologin")
    @ResponseBody
    public Result noLogin() {
        return Result.noLogin().msg("未登录!");
    }

    @RequestMapping(value = "/login")
    public String loginIndex() {
        return "login";
    }


    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public Result login(String username,
                        String password,
                        @RequestParam(value="rememberMe",required=false,defaultValue="false")Boolean rememberMe) throws Exception{
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password,rememberMe);
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);
            return Result.success().data(subject.getPrincipal()).msg("登录成功!");
        }
        catch (AuthenticationException e) {
            return Result.error().msg("用户名或密码错误");
        }
        catch (Exception e){
            log.error("登录异常",e);
            return Result.error();
        }
    }



}