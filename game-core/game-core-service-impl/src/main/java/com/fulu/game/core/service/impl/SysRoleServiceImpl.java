package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.SysRoleVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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

    /**
     * 分页获取角色列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<SysRole> list(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        SysRoleVO sysRoleVO = new SysRoleVO();
        sysRoleVO.setIsDel(0);
        List<SysRole> list = sysRoleDao.findByParameter(sysRoleVO);
        return new PageInfo(list);
    }
}
