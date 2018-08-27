package com.fulu.game.app.controller;


import com.fulu.game.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/open-api/v1")
public class HomeController extends BaseController {





    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result login(@RequestParam(required = true) String code,
                        @RequestParam(required = true) String mobile) {


        return null;

    }

}
