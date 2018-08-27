package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.vo.ReportVO;
import com.fulu.game.core.service.ReportService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户举报Controller
 *
 * @author Gong ZeChun
 * @date 2018/8/27 16:31
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
     * 举报列表
     *
     * @param reportVO 举报VO
     * @return 封装结果集
     */
    @RequestMapping("/list")
    public Result list(ReportVO reportVO) {
        PageInfo<ReportVO> voPageInfo = reportService.list(reportVO);
        return Result.success().data(voPageInfo).msg("查询成功！");
    }

    /**
     * 修改备注信息
     *
     * @param id     举报id
     * @param remark 备注
     * @return 封装结果集
     */
    @RequestMapping("/remark")
    public Result remark(@RequestParam Integer id, @RequestParam String remark) {
        boolean flag = reportService.remark(id, remark);
        if (flag) {
            return Result.success().msg("修改备注成功！");
        } else {
            return Result.error().msg("修改备注失败！");
        }
    }

    /**
     * 修改举报状态
     *
     * @param id 举报id
     * @return 封装结果集
     */
    @RequestMapping("/process")
    public Result process(@RequestParam Integer id) {
        boolean flag = reportService.process(id);
        if (flag) {
            return Result.success().msg("已成功处理！");
        } else {
            return Result.error().msg("处理失败！");
        }
    }
}
