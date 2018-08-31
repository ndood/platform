package com.fulu.game.core.dao;

import com.fulu.game.core.entity.UserInterests;
import com.fulu.game.core.entity.vo.UserInterestsVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 用户兴趣表
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-08-31 17:39:58
 */
@Mapper
public interface UserInterestsDao extends ICommonDao<UserInterests,Integer>{

    List<UserInterests> findByParameter(UserInterestsVO userInterestsVO);

}
