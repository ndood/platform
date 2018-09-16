package com.fulu.game.core.dao;

import com.fulu.game.core.entity.UserCommentTag;
import com.fulu.game.core.entity.vo.UserCommentTagVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-09-15 18:44:02
 */
@Mapper
public interface UserCommentTagDao extends ICommonDao<UserCommentTag,Integer>{

    List<UserCommentTag> findByParameter(UserCommentTagVO userCommentTagVO);




}
