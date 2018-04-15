package com.fulu.game.admin.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class HomeController {
    @RequestMapping({"/","/index"})
    public String index(){
        return"/index";
    }

    @RequestMapping("/login")
    public String login(HttpServletRequest request,
                        String username,
                        String password,
                        Map<String, Object> map) throws Exception{

        if(username!=null){
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            Subject subject = SecurityUtils.getSubject();

            subject.login(token);
            subject.checkRole("admin");
        }


        // 此方法不处理登录成功,由shiro进行处理
        return "/login";
    }



    @RequestMapping("/403")
    public String unauthorizedRole(){
        System.out.println("------没有权限-------");
        return "403";
    }

}