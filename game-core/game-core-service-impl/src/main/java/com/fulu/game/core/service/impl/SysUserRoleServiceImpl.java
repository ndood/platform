package com.fulu.game.core.service.impl;

import com.fulu.game.core.entity.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fulu.game.core.dao.SysUserRoleDao;
import com.fulu.game.core.service.SysUserRoleService;

import java.util.List;


@Service
public class SysUserRoleServiceImpl  implements SysUserRoleService {

    @Autowired
	private SysUserRoleDao sysUserRoleDao;


    public List<SysRole> findSysUserByRoleId(Integer userId){
        return sysUserRoleDao.findSysRoleByUserId(userId);
    }

	
}
