package com.fulu.game.core.service;

import com.fulu.game.core.entity.SysRole;
import com.fulu.game.core.entity.SysUserRole;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-04-15 19:14:30
 */
public interface SysUserRoleService {

    public List<SysRole> findSysUserByRoleId(Integer userId);
	
}
