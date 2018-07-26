package com.fulu.game.point.controller;

import com.fulu.game.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping(value = "/api/v1/grading")
public class GradingInfoController extends BaseController{


    @PostMapping(value = "")
    public Result gradingAreaInfo(){


        return Result.success();
    }


}
