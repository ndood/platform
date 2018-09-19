package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.AdminRoleDao;
import com.fulu.game.core.entity.AdminRole;
import com.fulu.game.core.service.AdminRoleService;



@Service
public class AdminRoleServiceImpl extends AbsCommonService<AdminRole,Integer> implements AdminRoleService {

    @Autowired
	private AdminRoleDao adminRoleDao;



    @Override
    public ICommonDao<AdminRole, Integer> getDao() {
        return adminRoleDao;
    }

    /**
     * 设置用户角色信息
     *
     * @param admin
     */
    @Override
    public void save(Admin admin) {
        AdminRole adminRole = adminRoleDao.findByAdminId(admin.getId());
        if(adminRole != null ){
            adminRole.setRoleId(admin.getRoleId());
            adminRoleDao.update(adminRole);
        } else {
            adminRole = new AdminRole();
            adminRole.setAdminId(admin.getId());
            adminRole.setRoleId(admin.getRoleId());
            adminRoleDao.create(adminRole);
        }
    }
}
