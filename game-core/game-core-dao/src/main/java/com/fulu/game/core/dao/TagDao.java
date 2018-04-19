package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Tag;

import java.util.List;
import java.util.Map;

import com.fulu.game.core.entity.vo.TagVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签表
 * @author wangbin
 * @date 2018-04-18 16:29:26
 */
@Mapper
public interface TagDao extends ICommonDao<Tag,Integer>{

    Tag findByParameter(TagVO tagVO);

}
