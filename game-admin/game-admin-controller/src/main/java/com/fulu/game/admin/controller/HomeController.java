package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {


    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String index(){
        return "index";
    }


    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String login() {
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
            return Result.success().msg("登录成功!");
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.error();
        }
    }


}