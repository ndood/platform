package com.fulu.game.app.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.vo.ReportVO;
import com.fulu.game.core.service.ReportService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 用户举报Controller
 *
 * @author Gong ZeChun
 * @date 2018/8/27 13:41
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/report")
public class ReportController extends BaseController {
    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * 提交举报内容
     *
     * @param reportVO 举报VO
     * @return 封装结果集
     */
    @RequestMapping("/submit")
    public Result submit(ReportVO reportVO) {
        reportService.submit(reportVO);
        return Result.success().msg("举报成功！");
    }

    /**
     * 举报列表
     *
     * @param status    处理状态： 0：未处理（默认），1：已处理，空：全部
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 封装结果集
     */
    @RequestMapping("/list")
    public Result list(Integer status, Date startTime, Date endTime) {
        PageInfo<ReportVO> voPageInfo = reportService.list(status, startTime, endTime);
        return Result.success().data(voPageInfo).msg("查询成功！");
    }
}
