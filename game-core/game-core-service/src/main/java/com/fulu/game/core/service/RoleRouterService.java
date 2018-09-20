package com.fulu.game.core.service;

import com.fulu.game.core.entity.RoleRouter;

import java.util.List;


/**
 * 角色路由表
 * 
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-09-19 16:32:36
 */
public interface RoleRouterService extends ICommonService<RoleRouter,Integer>{

    /**
     * 获取角色下的所有router
     * @param roleId
     * @return
     */
    List<RoleRouter> findByRoleId(Integer roleId);

    /**
     * 通过角色id删除当前角色所有的角色路由信息
     * @param id
     */
    void deleteByRoleId(Integer id);
}
