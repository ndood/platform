package com.fulu.game.core.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.SysRouterVO;
import com.fulu.game.core.service.util.SysRouterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.SysRouterDao;
import com.fulu.game.core.entity.SysRouter;
import com.fulu.game.core.service.SysRouterService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class SysRouterServiceImpl extends AbsCommonService<SysRouter,Integer> implements SysRouterService {

    @Autowired
	private SysRouterDao sysRouterDao;



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
     * @return
     */
    @Override
    public List<SysRouter> findSysRouterListByAdminId(Integer id) {
        List<SysRouter> list = sysRouterDao.findSysRouterListByAdminId(id);
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


}
