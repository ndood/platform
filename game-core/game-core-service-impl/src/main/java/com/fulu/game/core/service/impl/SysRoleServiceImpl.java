package com.fulu.game.core.service.impl;



import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fulu.game.core.dao.SysRoleDao;
import com.fulu.game.core.entity.SysRole;
import com.fulu.game.core.service.SysRoleService;



@Service
public class SysRoleServiceImpl extends AbsCommonService<SysRole,Integer> implements SysRoleService {

    @Autowired
	private SysRoleDao sysRoleDao;



    @Override
    public ICommonDao<SysRole, Integer> getDao() {
        return sysRoleDao;
    }
	
}
