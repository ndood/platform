package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.Admin;
import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.core.service.queue.RedisTaskContainer;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@Slf4j
public class HomeController {

    @Autowired
    private RedisTaskContainer redisTaskContainer;

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
            return Result.success().data(map).msg("登录成功!");
        } catch (AuthenticationException e) {
            log.error("shiro验证失败", e);
            return Result.error().msg("登录失败,请联系管理员");
        } catch (Exception e) {
            log.error("登录异常", e);
            return Result.error().msg("登录异常");
        }
    }


    @RequestMapping(value = "/test/queue", method = RequestMethod.GET)
    @ResponseBody
    public Result testQueue(String id,String content){
        redisTaskContainer.getRedisQueue().pushFromHead(new Msg(id,content));

        return Result.success();
    }

}