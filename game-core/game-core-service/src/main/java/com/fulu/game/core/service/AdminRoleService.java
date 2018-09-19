package com.fulu.game.core.service;

import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.AdminRole;



/**
 * 用户角色表
 * 
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-09-19 16:28:46
 */
public interface AdminRoleService extends ICommonService<AdminRole,Integer>{

    /**
     * 设置用户角色信息
     * @param admin
     */
    public void save(Admin admin);
}
