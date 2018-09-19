package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.SysRoleDao;
import com.fulu.game.core.entity.SysRole;
import com.fulu.game.core.service.SysRoleService;

import java.util.List;


@Service
public class SysRoleServiceImpl extends AbsCommonService<SysRole,Integer> implements SysRoleService {

    @Autowired
	private SysRoleDao sysRoleDao;



    @Override
    public ICommonDao<SysRole, Integer> getDao() {
        return sysRoleDao;
    }

    /**
     * 通过用户id获取角色列表
     *
     * @param id
     * @return
     */
    @Override
    public List<SysRole> findByAdminId(Integer id) {
        return sysRoleDao.findByAdminId(id);
    }
}
