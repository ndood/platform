package com.fulu.game.core.dao;

import com.fulu.game.core.entity.UserScoreDetails;
import com.fulu.game.core.entity.vo.UserScoreDetailsVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 用户积分详情表
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-07-17 14:15:01
 */
@Mapper
public interface UserScoreDetailsDao extends ICommonDao<UserScoreDetails,Integer>{

    List<UserScoreDetails> findByParameter(UserScoreDetailsVO userScoreDetailsVO);

}
