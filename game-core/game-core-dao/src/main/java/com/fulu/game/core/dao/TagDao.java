package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Tag;

import java.util.List;

import com.fulu.game.core.entity.vo.TagVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 标签表
 * @author wangbin
 * @date 2018-04-18 16:29:26
 */
@Mapper
public interface TagDao extends ICommonDao<Tag,Integer>{

    List<Tag> findByParameter(TagVO tagVO);

    /**
     * 根据条件删除表记录
     * @param pid
     * @return
     */
    boolean deleteByPid(Integer pid);


    List<Tag> findByTagIds(@Param(value = "tagIds") List<Integer> tagIds);

}
