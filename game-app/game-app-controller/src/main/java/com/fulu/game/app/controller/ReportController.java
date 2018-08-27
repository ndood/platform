package com.fulu.game.app.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.vo.ReportVO;
import com.fulu.game.core.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
