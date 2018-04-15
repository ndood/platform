package com.fulu.game.core.service;

import com.fulu.game.core.entity.SysPermission;
import com.fulu.game.core.entity.SysRolePermission;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-04-15 19:14:30
 */
public interface SysRolePermissionService {

    List<SysPermission> findSysPermissionByRoleId(Integer roleId);
}
