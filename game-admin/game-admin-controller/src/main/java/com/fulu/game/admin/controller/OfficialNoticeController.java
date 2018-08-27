package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.OfficialNotice;
import com.fulu.game.core.service.OfficialNoticeService;
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
 * @date 2018/8/27 18:27
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

    /**
     * 官方公告列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/list")
    public Result list(@RequestParam Integer pageNum,
                       @RequestParam Integer pageSize) {
        PageInfo<OfficialNotice> pageInfo = officialNoticeService.list(pageNum, pageSize);
        return Result.success().data(pageInfo).msg("查询成功！");
    }


    /**
     * 新增官方公告
     *
     * @param officialNotice 入参Bean
     * @return 封装结果集
     */
    @RequestMapping("/add")
    public Result add(OfficialNotice officialNotice) {
        officialNoticeService.add(officialNotice);
        return Result.success().msg("新增成功！");
    }
}
