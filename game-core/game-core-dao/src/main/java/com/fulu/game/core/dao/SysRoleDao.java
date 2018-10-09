package com.fulu.game.core.dao;

import com.fulu.game.core.entity.SysRole;
import com.fulu.game.core.entity.vo.SysRoleVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 角色表
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-09-19 16:25:01
 */
@Mapper
public interface SysRoleDao extends ICommonDao<SysRole,Integer>{

    List<SysRole> findByParameter(SysRoleVO sysRoleVO);

    /**
     * 通过用户id获取角色列表
     * @param id
     * @return
     */
    List<SysRole> findByAdminId(Integer id);
}
