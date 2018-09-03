package com.fulu.game.core.dao;

import com.fulu.game.core.entity.DynamicComment;
import com.fulu.game.core.entity.vo.DynamicCommentVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 动态评论表
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-08-30 11:21:42
 */
@Mapper
public interface DynamicCommentDao extends ICommonDao<DynamicComment,Long>{

    List<DynamicComment> findByParameter(DynamicCommentVO dynamicCommentVO);

    /**
     * 获取评论列表
     * @param dynamicCommentVO
     * @return
     */
    List<DynamicCommentVO> list(DynamicCommentVO dynamicCommentVO);
}
