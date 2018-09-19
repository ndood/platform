package com.fulu.game.core.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.fulu.game.core.dao.ICommonDao;
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
        List<SysRouter> resultList = new ArrayList<>();
        for (SysRouter router1 : list) {
            boolean flag = false;
            for (SysRouter router2 : list) {
                if(router1.getPid() != null)
                {
                    if (router1.getPid().equals(router2.getId())) {
                        flag = true;
                        if (router2.getChild() == null) {
                            router2.setChild(new ArrayList<SysRouter>());
                        }
                        router2.getChild().add(router1);
                        break;
                    }
                }
            }
            if (!flag) {
                resultList.add(router1);
            }
        }

        return resultList;
    }
}
