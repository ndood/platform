package com.fulu.game.core.service;

import com.fulu.game.core.entity.Tag;
import com.fulu.game.core.entity.vo.TagVO;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * 标签表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-04-18 16:29:26
 */
public interface TagService extends ICommonService<Tag,Integer>{


    Tag create(Integer categoryId, String tagName);

    Tag update(Integer id, String tagName,Integer gender);

    PageInfo<Tag> parentList(Integer pageNum, Integer pageSize);

    List<Tag> findByPid(Integer tagPid);



    /**
     * 查询所有个人标签
     * @return
     */
    List<Tag> findAllPersonTags();

    /**
     * 查询标签组和子标签
     * @param tagPid
     * @return
     */
    TagVO  findTagsByTagPid(Integer tagPid);
}
