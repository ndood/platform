package com.fulu.game.core.dao;

import com.fulu.game.core.entity.SysUnit;
import com.fulu.game.core.entity.vo.SysUnitVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 系统单位表
 * @author wangbin
 * @email ${email}
 * @date 2018-07-31 11:18:12
 */
@Mapper
public interface SysUnitDao extends ICommonDao<SysUnit,Integer>{

    List<SysUnit> findByParameter(SysUnitVO sysUnitVO);

}
