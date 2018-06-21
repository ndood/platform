package com.fulu.game.admin.controller;

import com.fulu.game.core.entity.ImUser;
import com.fulu.game.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

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

    public void imRegistAndBind() {
        List<ImUser> list = userService.findImNullUser();
        //todo 批量注册IMID
        //todo 绑定到user中

    }
}
