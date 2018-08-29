package com.fulu.game.app.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.PushMsg;
import com.fulu.game.core.service.PushMsgService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 官方公告Controller
 *
 * @author Gong ZeChun
 * @date 2018/8/29 11:17
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/pushmsg")
public class PushMsgController extends BaseController {

    private final PushMsgService pushMsgService;

    @Autowired
    public PushMsgController(PushMsgService pushMsgService) {
        this.pushMsgService = pushMsgService;
    }

    /**
     * 官方公告列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/official-notice/list")
    public Result officialNoticeList(@RequestParam Integer pageNum,
                                     @RequestParam Integer pageSize) {
        PageInfo<PushMsg> pageInfo = pushMsgService.officialNoticeList(pageNum, pageSize);
        return Result.success().data(pageInfo).msg("查询成功！");
    }
}
