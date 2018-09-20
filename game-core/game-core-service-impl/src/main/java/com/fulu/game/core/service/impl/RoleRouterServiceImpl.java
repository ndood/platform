package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.RoleRouterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.RoleRouterDao;
import com.fulu.game.core.entity.RoleRouter;
import com.fulu.game.core.service.RoleRouterService;

import java.util.List;


@Service
public class RoleRouterServiceImpl extends AbsCommonService<RoleRouter,Integer> implements RoleRouterService {

    @Autowired
	private RoleRouterDao roleRouterDao;



    @Override
    public ICommonDao<RoleRouter, Integer> getDao() {
        return roleRouterDao;
    }

    /**
     * 获取角色下的所有router
     *
     * @param roleId
     * @return
     */
    @Override
    public List<RoleRouter> findByRoleId(Integer roleId) {
        RoleRouterVO roleRouterVO = new RoleRouterVO();
        roleRouterVO.setRoleId(roleId);
        return roleRouterDao.findByParameter(roleRouterVO);
    }

    /**
     * 通过角色id删除当前角色所有的角色路由信息
     *
     * @param id
     */
    @Override
    public void deleteByRoleId(Integer id) {
        roleRouterDao.deleteByRoleId(id);
    }
}
