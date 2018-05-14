package com.fulu.game.core.dao;

import com.fulu.game.core.entity.SysConfig;
import com.fulu.game.core.entity.vo.SysConfigVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置表
 * @author wangbin
 * @email ${email}
 * @date 2018-05-14 14:54:03
 */
@Mapper
public interface SysConfigDao extends ICommonDao<SysConfig,Integer>{

    List<SysConfig> findByParameter(SysConfigVO sysConfigVO);

}
