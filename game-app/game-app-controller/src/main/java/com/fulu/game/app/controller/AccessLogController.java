package com.fulu.game.app.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.AccessLog;
import com.fulu.game.core.entity.vo.AccessLogVO;
import com.fulu.game.core.entity.vo.DynamicCommentVO;
import com.fulu.game.core.service.AccessLogService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/9/3 9:19.
 * @Description: 访问日志
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/access-log")
public class AccessLogController {

    @Autowired
    private AccessLogService accessLogService;


    /**
     * 获取来访记录列表接口
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "access-list")
    public Result accessList(@RequestParam(required = true) Integer pageNum,
                       @RequestParam(required = true) Integer pageSize) {
        PageInfo<AccessLogVO> page = accessLogService.accessList( pageNum, pageSize);
        return Result.success().data(page);
    }

    /**
     * 获取足迹记录列表接口
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "footprint-list")
    public Result footprintList(@RequestParam(required = true) Integer pageNum,
                       @RequestParam(required = true) Integer pageSize) {
        PageInfo<AccessLogVO> page = accessLogService.footprintList( pageNum, pageSize);
        return Result.success().data(page);
    }

}
