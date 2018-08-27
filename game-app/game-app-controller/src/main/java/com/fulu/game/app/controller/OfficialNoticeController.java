package com.fulu.game.app.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.service.OfficialNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 官方公告Controller
 *
 * @author Gong ZeChun
 * @date 2018/8/27 18:15
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/official-notice")
public class OfficialNoticeController extends BaseController {
    private final OfficialNoticeService officialNoticeService;

    @Autowired
    public OfficialNoticeController(OfficialNoticeService officialNoticeService) {
        this.officialNoticeService = officialNoticeService;
    }

    @RequestMapping("/list")
    public Result list(@RequestParam Integer pageNum,
                       @RequestParam Integer pageSize) {


        return null;
    }
}
