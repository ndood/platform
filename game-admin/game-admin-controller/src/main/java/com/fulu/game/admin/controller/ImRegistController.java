package com.fulu.game.admin.controller;

import com.fulu.game.core.entity.ImUser;
import com.fulu.game.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 服务端注册im
 *
 * @author yanbiao
 * @date 2018.06.21
 */
@Controller
@Slf4j
@RequestMapping("/im")
public class ImRegistController {

    @Autowired
    private UserService userService;

    @RequestMapping("/regist")
    public void regist() {
        List<ImUser> list = userService.findImNullUser();
        //环信限流批量注册数每次20个
        for(int i=0;i<=20;i++){

        }

    }
    private void bind(List<ImUser> list){
        //todo 绑定到user中
    }
}
