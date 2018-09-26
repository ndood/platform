package com.fulu.game.core.dao;

import com.fulu.game.core.entity.ServerComment;
import com.fulu.game.core.entity.vo.ServerCommentVO;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 陪玩师评价用户表
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-09-20 11:26:51
 */
@Mapper
public interface ServerCommentDao extends ICommonDao<ServerComment,Integer>{

    List<ServerComment> findByParameter(ServerCommentVO serverCommentVO);

    /**
     * 查询陪玩师对用户的综合评分（评分平均值）
     * @param userId
     * @return
     */
    BigDecimal findScoreAvgByUserId(Integer userId);
}
