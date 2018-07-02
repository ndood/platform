package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.StatusEnum;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.Advice;
import com.fulu.game.core.entity.vo.AdviceVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.AdviceService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/api/v1/advice")
public class AdviceController {

    @Autowired
    private AdviceService adviceService;
    @Autowired
    private AdminService adminService;

    /**
     * 查询建议列表
     *
     * @param pageNum
     * @param pageSize
     * @param adviceVO
     * @return
     */
    @PostMapping("/list")
    public Result list(@RequestParam("pageNum") Integer pageNum,
                       @RequestParam("pageSize") Integer pageSize,
                       AdviceVO adviceVO) {
        PageInfo<AdviceVO> page = adviceService.list(pageNum, pageSize, adviceVO);
        return Result.success().data(page).msg("查询成功");
    }

    /**
     * 建议状态置为已处理
     *
     * @param adviceId
     * @param remark
     * @return
     */
    @PostMapping("/handle")
    public Result handle(@RequestParam("adviceId") Integer adviceId,
                         @RequestParam("remark") String remark) {
        Admin admin = adminService.getCurrentUser();
        int adminId = admin.getId();
        log.info("调用用户建议处理接口，操作人adminId={}，入参adviceId={}", adminId, adviceId);
        Advice advice = adviceService.findById(adviceId);
        if (advice == null) {
            return Result.error().msg("未查询到该条记录");
        }
        if (StringUtils.isBlank(remark)) {
            return Result.error().msg("请填写备注");
        }
        if (advice.getStatus().equals(StatusEnum.ADVICE_SOLVED.getType())) {
            return Result.error().msg("建议已是处理状态");
        }
        advice.setAdminId(adminId);
        advice.setAdminName(admin.getName());
        advice.setRemark(remark);
        advice.setStatus(StatusEnum.ADVICE_SOLVED.getType());
        advice.setUpdateTime(new Date());
        adviceService.update(advice);
        return Result.success().data(advice).msg("处理成功");
    }

    /**
     * 建议状态置为标记
     *
     * @param adviceId
     * @param remark
     * @return
     */
    @PostMapping("/mark")
    public Result mark(@RequestParam("adviceId") Integer adviceId,
                       @RequestParam("remark") String remark) {
        Admin admin = adminService.getCurrentUser();
        int adminId = admin.getId();
        log.info("调用用户建议标记接口，操作人adminId={}，入参adviceId={}", adminId, adviceId);
        Advice advice = adviceService.findById(adviceId);
        if (advice == null) {
            return Result.error().msg("未查询到该条记录");
        }
        if (StringUtils.isBlank(remark)) {
            return Result.error().msg("请填写备注");
        }
        if (!advice.getStatus().equals(StatusEnum.ADVICE_WAIT.getType())) {
            return Result.error().msg("未处理状态的建议才能标记");
        }
        advice.setAdminId(adminId);
        advice.setAdminName(admin.getName());
        advice.setRemark(remark);
        advice.setStatus(StatusEnum.ADVICE_MARKED.getType());
        advice.setUpdateTime(new Date());
        adviceService.update(advice);
        return Result.success().data(advice).msg("标记成功");
    }
}
