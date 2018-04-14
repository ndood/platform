package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by bwang.abcft on 2018/4/13.
 */
@Controller
public class IndexController {



    @GetMapping(value = "/")
    @ResponseBody
    public Result index(){
        return Result.success().msg("hello");
    }


}
