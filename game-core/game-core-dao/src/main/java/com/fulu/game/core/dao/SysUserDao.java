package com.fulu.game.core.dao;

import com.fulu.game.core.entity.SysUser;



import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author wangbin
 * @date 2018-04-15 19:14:30
 */
@Mapper
public interface SysUserDao extends ICommonDao<SysUser,Integer>{


    public SysUser findByUsername(String userName);

}
