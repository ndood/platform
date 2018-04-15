package com.fulu.game.core.dao;


import com.fulu.game.core.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-04-15 19:14:30
 */
@Mapper
public interface SysRolePermissionDao {

     List<SysPermission> findSysPermissionByRoleId(Integer roleId);

}
