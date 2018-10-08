package com.fulu.game.core.service;

import com.fulu.game.core.entity.SysRouter;
import com.fulu.game.core.entity.vo.SysRouterVO;
import com.github.pagehelper.PageInfo;

import java.util.List;


/**
 * 路由表
 * 
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-09-19 16:21:35
 */
public interface SysRouterService extends ICommonService<SysRouter,Integer>{

    /**
     * 通过角色id获取角色对应router集合
     * @param id
     * @return
     */
    List<SysRouter> findByRoleId(Integer id);

    /**
     * 获取用户菜单列表
     * @param id
     * @return
     */
    List<SysRouter> findSysRouterListByAdminId(Integer id);

    /**
     * 通过type获取Router集合
     * @param type
     * @return
     */
    List<SysRouter> findByType(Integer type);

    /**
     * 获取用户所有router，用户id不传表示获取所有
     * @param userId
     * @return
     */
    List<SysRouter> findByUserId(Integer userId);

    /**
     * 保存router信息
     * @param sysRouterVO
     */
    void save(SysRouterVO sysRouterVO);
}
