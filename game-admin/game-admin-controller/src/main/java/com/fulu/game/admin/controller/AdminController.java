package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.vo.AdminVO;
import com.fulu.game.core.service.AdminService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/admin")
public class AdminController extends BaseController{

    @Autowired
    private AdminService adminService;

    /**
     * 查询-管理员-列表
     * @param adminVO
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/list")
    public Result list(@ModelAttribute AdminVO adminVO,
                             @RequestParam("pageNum") Integer pageNum,
                             @RequestParam("pageSize") Integer pageSize){
        PageInfo<Admin> adminList = adminService.list(adminVO,pageNum,pageSize);
        return Result.success().data(adminList);
    }

    @RequestMapping("/save")
    public Result save(AdminVO adminVO){
        adminService.save(adminVO);
        return Result.success().data(adminVO);
    }

}
