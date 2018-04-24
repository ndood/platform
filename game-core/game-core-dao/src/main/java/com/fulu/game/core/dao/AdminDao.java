package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.vo.AdminVO;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 系统管理员表
 * @author yanbiao
 * @email ${email}
 * @date 2018-04-24 10:20:44
 */
@Mapper
public interface AdminDao extends ICommonDao<Admin,Integer>{

    List<Admin> findByParameter(AdminVO adminVO);
}
