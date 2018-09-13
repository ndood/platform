package com.fulu.game.app.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.DynamicLike;
import com.fulu.game.core.entity.DynamicPushMsg;
import com.fulu.game.core.service.DynamicPushMsgService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/9/11 10:45.
 * @Description:
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/dynamic-push-msg")
public class DynamicPushMsgController extends BaseController {

    @Autowired
    private DynamicPushMsgService dynamicPushMsgService;

    /**
     * 获取消息推送记录接口
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "list")
    public Result list(@RequestParam(required = true) Integer pageNum,
                       @RequestParam(required = true) Integer pageSize) {
        PageInfo<DynamicPushMsg> page = dynamicPushMsgService.list( pageNum, pageSize);
        return Result.success().data(page);
    }
}
