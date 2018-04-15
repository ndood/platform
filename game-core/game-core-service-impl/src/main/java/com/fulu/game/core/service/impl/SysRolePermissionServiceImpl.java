package com.fulu.game.core.service.impl;




import com.fulu.game.core.entity.SysPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.SysRolePermissionDao;
import com.fulu.game.core.entity.SysRolePermission;
import com.fulu.game.core.service.SysRolePermissionService;

import java.util.List;


@Service
public class SysRolePermissionServiceImpl implements SysRolePermissionService {

    @Autowired
	private SysRolePermissionDao sysRolePermissionDao;


    public List<SysPermission> findSysPermissionByRoleId(Integer roleId){
      return sysRolePermissionDao.findSysPermissionByRoleId(roleId);
    }


}
