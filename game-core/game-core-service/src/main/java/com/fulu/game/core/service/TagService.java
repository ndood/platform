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

    /**
     * 创建用户自定义标签
     * @param userId
     * @param categoryId
     * @param tagName
     * @return
     */
    Tag createUserCustomTag(int userId,int categoryId,String tagName);

     void delUserTag(Integer userId, int tagId);

    Tag update(Integer id, String tagName,Integer gender);

    PageInfo<Tag> parentList(Integer pageNum, Integer pageSize);

    List<Tag> findByPid(int tagPid);

    /**
     * 查找所有用户标签
     * @param userId
     * @param categoryId
     * @return
     */
    List<Tag> findUserTags(int userId,int categoryId);
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
    List<Tag> findCategoryParentTags(int categoryId);

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
    TagVO findTagsByCategoryId(Integer categoryId);

    /**
     * 查询用户自定义表情和游戏分类标签
     * @param userId
     * @param categoryId
     * @return
     */
    List<Tag> findByUserCategoryId(int userId,int categoryId);
    /**
     * 内容管理-删除标签组（和相关子标签）
     * @param tag
     * @return
     */
    Boolean delGroupTag(Tag tag);

    List<Tag> findByTagIds(List<Integer> tagIds);
}
