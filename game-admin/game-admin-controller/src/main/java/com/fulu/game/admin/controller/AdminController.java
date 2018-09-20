package com.fulu.game.admin.controller;

import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.SysRole;
import com.fulu.game.core.entity.vo.AdminVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.SysRoleService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/admin")
public class AdminController extends BaseController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private SysRoleService sysRoleService;

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
        adminVO.setAdminName(Constant.ADMIN_USERNAME);
        PageInfo<Admin> adminList = adminService.list(adminVO, pageNum, pageSize);
        return Result.success().data(adminList).msg("查询列表成功！");
    }

    /**
     * 新增/修改-管理员
     *
     * @param adminVO
     * @return
     */
    @PostMapping("/save")
    public Result save(AdminVO adminVO) {
        Admin admin = adminService.save(adminVO);
        String msgPrefix = adminVO.getId() != null && adminVO.getId().intValue() > 0 ? "修改" : "新增";
        return Result.success().data(admin).msg(msgPrefix + "管理员成功！");
    }

    /**
     * 删除-管理员
     *
     * @param adminId
     * @return
     */
    @PostMapping("/del")
    public Result delete(@RequestParam("adminId") Integer adminId) {
        adminService.deleteById(adminId);
        return Result.success().msg("删除管理员成功！");
    }

    /**
     * 查询-管理员
     *
     * @param adminId
     * @return
     */
    @PostMapping("/query")
    public Result query(@RequestParam("adminId") Integer adminId) {
        Admin admin = adminService.findById(adminId);
        return Result.success().data(admin).msg("查询管理员成功！");
    }

    @PostMapping("/lock")
    public Result lock(@RequestParam("adminId") Integer adminId) {
        adminService.lock(adminId);
        return Result.success().msg("操作成功");
    }

    /**
     * 查询-所有角色-列表
     *
     * @return
     */
    @PostMapping("sys-role/all-list")
    public Result sysRoleAllList() {
        List<SysRole> list = sysRoleService.findAll();
        return Result.success().data(list).msg("查询列表成功！");
    }

    /**
     * 分页查询-角色-列表
     *
     * @return
     */
    @PostMapping("sys-role/list")
    public Result sysRoleList(@RequestParam("pageNum") Integer pageNum,
                              @RequestParam("pageSize") Integer pageSize) {
        PageInfo<SysRole> list = sysRoleService.list(pageNum,pageSize);
        return Result.success().data(list).msg("查询列表成功！");
    }


}
