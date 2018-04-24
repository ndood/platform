package com.fulu.game.core.service;

import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.vo.AdminVO;
import com.github.pagehelper.PageInfo;

/**
 * 系统管理员表
 * 
 * @author yanbiao
 * @email ${email}
 * @date 2018-04-24 10:20:44
 */
public interface AdminService extends ICommonService<Admin,Integer>{

    Admin findByUsername(String username);

    PageInfo<Admin> list(AdminVO adminVO, Integer pageNum, Integer pageSize);

    void save(AdminVO adminVO);
}
