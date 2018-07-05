package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.vo.AdminVO;
import com.fulu.game.core.service.AdminService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/admin")
public class AdminController extends BaseController {

    @Autowired
    private AdminService adminService;

    /**
     * 查询-管理员-列表
     *
     * @param adminVO
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping("/list")
    public Result list(AdminVO adminVO,
                       @RequestParam("pageNum") Integer pageNum,
                       @RequestParam("pageSize") Integer pageSize) {
        PageInfo<Admin> adminList = adminService.list(adminVO, pageNum, pageSize);
        return Result.success().data(adminList).msg("查询列表成功！");
    }

    /**
     * 新增-管理员
     *
     * @param adminVO
     * @return
     */
    @PostMapping("/save")
    public Result save(AdminVO adminVO) {
        Admin admin = adminService.save(adminVO);
        return Result.success().data(admin).msg("新增管理员成功！");
    }

    @PostMapping("/lock")
    public Result lock(@RequestParam("adminId") Integer adminId) {
        adminService.lock(adminId);
        return Result.success().msg("操作成功");
    }

}
