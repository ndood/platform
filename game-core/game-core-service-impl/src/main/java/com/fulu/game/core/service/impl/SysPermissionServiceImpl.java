package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import com.fulu.game.core.dao.SysPermissionDao;
import com.fulu.game.core.entity.SysPermission;
import com.fulu.game.core.service.SysPermissionService;



@Service
public class SysPermissionServiceImpl extends AbsCommonService<SysPermission,Integer> implements SysPermissionService {

    @Autowired
	private SysPermissionDao sysPermissionDao;



    @Override
    public ICommonDao<SysPermission, Integer> getDao() {
        return sysPermissionDao;
    }
	
}
