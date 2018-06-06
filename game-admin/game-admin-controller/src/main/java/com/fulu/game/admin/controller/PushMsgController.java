package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.PushMsg;
import com.fulu.game.core.entity.vo.PushMsgVO;
import com.fulu.game.core.service.PushMsgService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/api/v1/pushmsg")
public class PushMsgController extends BaseController{

    @Autowired
    private PushMsgService pushMsgService;

    /**
     * 推送微信消息
     * @param pushMsgVO
     * @return
     */
    @PostMapping(value = "/push")
    public Result push(@Valid PushMsgVO pushMsgVO){
        pushMsgService.push(pushMsgVO);
        return Result.success();
    }

    /**
     * 推送消息列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/list")
    public Result list(Integer pageNum,Integer pageSize){
        PageInfo<PushMsg> page =  pushMsgService.list(pageNum,pageSize,null);
        return Result.success().data(page);
    }



}
