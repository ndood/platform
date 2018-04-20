package com.fulu.game.core.dao;

import com.fulu.game.core.entity.PersonTag;
import com.fulu.game.core.entity.vo.PersonTagVO;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 个人标签关联表
 * @author wangbin
 * @email ${email}
 * @date 2018-04-20 15:50:34
 */
@Mapper
public interface PersonTagDao extends ICommonDao<PersonTag,Integer>{

    List<PersonTag> findByParameter(PersonTagVO personTagVO);

}
