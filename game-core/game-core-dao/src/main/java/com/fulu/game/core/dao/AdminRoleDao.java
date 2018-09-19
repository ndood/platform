package com.fulu.game.core.dao;

import com.fulu.game.core.entity.AdminRole;
import com.fulu.game.core.entity.vo.AdminRoleVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色表
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-09-19 16:28:46
 */
@Mapper
public interface AdminRoleDao extends ICommonDao<AdminRole,Integer>{

    List<AdminRole> findByParameter(AdminRoleVO adminRoleVO);

    /**
     * 获取用户角色关联信息
     * @param id
     * @return
     */
    AdminRole findByAdminId(Integer id);
}
