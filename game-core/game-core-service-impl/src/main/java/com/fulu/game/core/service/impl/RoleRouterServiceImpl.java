package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.RoleRouterDao;
import com.fulu.game.core.entity.RoleRouter;
import com.fulu.game.core.service.RoleRouterService;



@Service
public class RoleRouterServiceImpl extends AbsCommonService<RoleRouter,Integer> implements RoleRouterService {

    @Autowired
	private RoleRouterDao roleRouterDao;



    @Override
    public ICommonDao<RoleRouter, Integer> getDao() {
        return roleRouterDao;
    }
	
}
