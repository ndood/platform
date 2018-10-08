package com.fulu.game.core.dao;

import com.fulu.game.core.entity.SysRouter;
import com.fulu.game.core.entity.vo.SysRouterVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 路由表
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-09-19 16:21:35
 */
@Mapper
public interface SysRouterDao extends ICommonDao<SysRouter,Integer>{

    List<SysRouter> findByParameter(SysRouterVO sysRouterVO);

    /**
     * 通过角色id获取角色对应router集合
     * @param id
     * @return
     */
    List<SysRouter> findByRoleId(Integer id);

    /**
     * 获取菜单列表
     * @param id
     * @return
     */
    List<SysRouter> findSysRouterListByAdminId(Integer id);
}
