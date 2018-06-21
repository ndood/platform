package com.fulu.game.admin.controller;

import com.fulu.game.core.entity.ImUser;
import com.fulu.game.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 服务端注册im
 *
 * @author yanbiao
 * @date 2018.06.21
 */
public class ImRegistController {

    @Autowired
    private UserService userService;

    @RequestMapping("/regist")
    public void regist() {
        List<ImUser> list = userService.findImNullUser();
        //todo 批量注册IMID每次20个

    }
    private void bind(List<ImUser> list){
        //todo 绑定到user中
    }
}
