package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserComment;
import com.fulu.game.core.entity.UserCommentTag;
import com.github.pagehelper.PageInfo;

import java.util.List;


/**
 * 
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-09-15 18:44:02
 */
public interface UserCommentTagService extends ICommonService<UserCommentTag,Integer>{


    public void create(UserComment comment, List<Integer> tagIds);

    public List<UserCommentTag> findByCommentId(int commentId);

    PageInfo<UserCommentTag> findByServerId(Integer pageNum, Integer pageSize, Integer serverId);
}
