package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.OfficialActivity;
import com.fulu.game.core.entity.vo.OfficialActivityVO;
import com.fulu.game.core.service.OfficialActivityService;
import com.github.pagehelper.PageInfo;
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
        return Result.success().msg("活动创建成功!");
    }


    @RequestMapping(value = "delete")
    public Result delete(@RequestParam(required = true) Integer activityId){
        officialActivityService.delete(activityId);
        return Result.success().msg("活动删除成功");
    }


    @RequestMapping(value = "list")
    public Result list(@RequestParam(required = true) Integer pageNum,Integer pageSize){
        PageInfo<OfficialActivity> page =  officialActivityService.list(pageNum,pageSize);
        return Result.success().data(page);
    }




    @RequestMapping(value = "activate")
    public Result activate(@RequestParam(required = true) Integer activityId,
                         @RequestParam(required = true)Boolean activate){
        officialActivityService.activate(activityId,activate);
        String msg = "下架成功!";
        if(activate){
            msg ="上架成功!";
        }
        return Result.success().msg(msg);
    }



}

