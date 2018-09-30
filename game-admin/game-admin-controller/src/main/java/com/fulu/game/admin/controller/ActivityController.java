package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.vo.OfficialActivityVO;
import com.fulu.game.core.service.OfficialActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequestMapping("/api/v1/activity")
public class ActivityController extends BaseController{

    @Autowired
    private OfficialActivityService officialActivityService;




    @RequestMapping(value = "create")
    public Result create(OfficialActivityVO officialActivityVO){
        officialActivityService.create(officialActivityVO.getType(),officialActivityVO,officialActivityVO.getRedeemCodes());
        return Result.success();
    }




    @RequestMapping(value = "delete")
    public Result create(@RequestParam(required = true) Integer activityId){
        officialActivityService.delete(activityId);
        return Result.success();
    }




}

