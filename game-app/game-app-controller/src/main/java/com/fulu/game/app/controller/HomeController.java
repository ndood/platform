package com.fulu.game.app.controller;


import com.fulu.game.common.domain.ClientInfo;
import com.fulu.game.common.utils.SubjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping()
public class HomeController extends BaseController {


    /**
     * 查询所有分期乐业务
     *
     * @return 封装结果集
     */
    @PostMapping(value = "all")
    public void list(HttpServletRequest request) {
        ClientInfo clientInfo = SubjectUtil.getUserClientInfo();
        System.out.println(clientInfo);
    }

}
