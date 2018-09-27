package com.fulu.game.core.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.fulu.game.common.Constant;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.vo.SysRouterVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.util.SysRouterUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.SysRouterDao;
import com.fulu.game.core.entity.SysRouter;
import com.fulu.game.core.service.SysRouterService;

import java.util.*;


@Service
public class SysRouterServiceImpl extends AbsCommonService<SysRouter,Integer> implements SysRouterService {

    @Autowired
	private SysRouterDao sysRouterDao;

    @Autowired
    private AdminService adminService;



    @Override
    public ICommonDao<SysRouter, Integer> getDao() {
        return sysRouterDao;
    }

    /**
     * 通过角色id获取角色对应router集合
     *
     * @param id
     * @return
     */
    @Override
    public List<SysRouter> findByRoleId(Integer id) {
        return sysRouterDao.findByRoleId(id);
    }

    /**
     * 获取用户菜单列表
     *
     * @param id
     * @return 转为父子级关系
     */
    @Override
    public List<SysRouter> findSysRouterListByAdminId(Integer id) {
        Admin admin = adminService.findById(id);
        List<SysRouter> list = new ArrayList<>();
        // 非超级管理员只获取权限之内的router
        if(admin != null && !Constant.ADMIN_USERNAME.equals(admin.getUsername())){
            list = sysRouterDao.findSysRouterListByAdminId(id);
        } else {
            list = findAll();
        }
        return SysRouterUtil.formatRouter(list);
    }

    /**
     * 通过type获取Router集合
     *
     * @param type
     * @return
     */
    @Override
    public List<SysRouter> findByType(Integer type) {
        SysRouterVO sysRouterVO = new SysRouterVO();
        sysRouterVO.setType(type);
        sysRouterVO.setIsDel(0);
        return sysRouterDao.findByParameter(sysRouterVO);
    }

    /**
     * 获取用户所有router，用户id不传表示获取所有
     *
     * @param userId
     * @return 不转父子级关系
     */
    @Override
    public List<SysRouter> findByUserId(Integer userId) {
        Admin admin = adminService.findById(userId);
        if(admin != null && !Constant.ADMIN_USERNAME.equals(admin.getUsername())){
            return sysRouterDao.findSysRouterListByAdminId(userId);
        } else {
            return findAll();
        }
    }

    /**
     * 保存router信息
     *
     * @param sysRouterVO
     */
    @Override
    public void save(SysRouterVO sysRouterVO) {
        SysRouter sysRouter = new SysRouter();
        BeanUtil.copyProperties(sysRouterVO, sysRouter);
        SysRouterVO params = new SysRouterVO();
        params.setName(sysRouterVO.getName());
        params.setId(sysRouterVO.getId());
        List<SysRouter> sysRouterList= sysRouterDao.findByParameter(params);
        if(!CollectionUtil.isEmpty(sysRouterList)){
            throw new UserException(UserException.ExceptionCode.ROUTER_NAME_EXIST);
        }
        Admin admin = adminService.getCurrentUser();
        sysRouter.setOperatorId(admin.getId());
        sysRouter.setOperatorName(admin.getName());
        sysRouter.setUpdateTime(new Date());
        if(sysRouterVO.getId() != null && sysRouterVO.getId().intValue() > 0){
            sysRouterDao.update(sysRouter);
        } else {
            sysRouter.setCreateTime(new Date());
            sysRouter.setIsDel(0);
            sysRouterDao.create(sysRouter);
        }
    }


}
