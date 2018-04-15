package com.fulu.game.core.service.impl;



import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import com.fulu.game.core.dao.SysUserDao;
import com.fulu.game.core.entity.SysUser;



@Service
public class SysUserServiceImpl extends AbsCommonService<SysUser,Integer> implements SysUserService {

    @Autowired
	private SysUserDao sysUserDao;

    @Override
    public ICommonDao<SysUser, Integer> getDao() {
        return sysUserDao;
    }

    @Override
    public SysUser findByUsername(String userName) {
        return sysUserDao.findByUsername(userName);
    }
}
