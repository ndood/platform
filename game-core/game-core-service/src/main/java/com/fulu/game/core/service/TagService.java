package com.fulu.game.core.service;

import com.fulu.game.core.entity.Tag;
import com.fulu.game.core.entity.vo.TagVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 标签表
 *
 * @author wangbin
 * @email ${email}
 * @date 2018-04-18 16:29:26
 */
public interface TagService extends ICommonService<Tag,Integer>{


    Tag create(Integer categoryId, Integer pid, String tagName);

    Tag update(Integer id, String tagName,Integer gender);

    PageInfo<Tag> parentList(Integer pageNum, Integer pageSize);

    List<Tag> findByPid(int tagPid);

    List<Tag> findGroupTagByCategoryId(int categoryId);

    /**
     * 查询所有个人标签
     * @return
     */
    List<Tag> findAllPersonTags();

    /**
     * 查询所有游戏标签
     * @param categoryId
     * @return
     */
    List<Tag> findAllCategoryTags(int categoryId);

    /**
     * 查询标签组和子标签
     * @param tagPid
     * @return
     */
    TagVO  findTagsByTagPid(Integer tagPid);

    /**
     * 兼容老接口的category标签查询
     * @param categoryId
     * @return
     */
    TagVO  oldFindTagsByCategoryId(Integer categoryId);

    /**
     * 内容管理-删除标签组（和相关子标签）
     * @param tag
     * @return
     */
    Boolean delGroupTag(Tag tag);

    List<Tag> findByTagIds(List<Integer> tagIds);
}
