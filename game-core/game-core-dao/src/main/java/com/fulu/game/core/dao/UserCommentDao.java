package com.fulu.game.core.dao;

import com.fulu.game.core.entity.UserComment;
import com.fulu.game.core.entity.vo.UserCommentVO;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 用户(打手)星级评论表
 * @author yanbiao
 * @date 2018-04-30 13:19:26
 */
@Mapper
public interface UserCommentDao extends ICommonDao<UserComment,Integer>{

    List<UserComment> findByParameter(UserCommentVO userCommentVO);

    List<UserCommentVO> findVOByParameter(UserCommentVO userCommentVO);

    void callScoreAvgProc(Integer id);

    BigDecimal findScoreAvgByServerUserId(int serverUserId);
}
