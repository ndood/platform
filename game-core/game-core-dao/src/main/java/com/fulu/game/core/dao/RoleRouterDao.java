package com.fulu.game.core.dao;

import com.fulu.game.core.entity.RoleRouter;
import com.fulu.game.core.entity.vo.RoleRouterVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 角色路由表
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-09-19 16:32:36
 */
@Mapper
public interface RoleRouterDao extends ICommonDao<RoleRouter,Integer>{

    List<RoleRouter> findByParameter(RoleRouterVO roleRouterVO);

    /**
     * 通过角色id删除当前角色所有的角色路由信息
     * @param id
     */
    void deleteByRoleId(Integer id);
}
