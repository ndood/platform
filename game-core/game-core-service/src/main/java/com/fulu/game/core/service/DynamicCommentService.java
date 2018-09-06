package com.fulu.game.core.service;

import com.fulu.game.core.entity.DynamicComment;
import com.fulu.game.core.entity.vo.DynamicCommentVO;
import com.github.pagehelper.PageInfo;


/**
 * 动态评论表
 * 
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-08-30 11:21:42
 */
public interface DynamicCommentService extends ICommonService<DynamicComment,Integer>{

    /**
     * 评论接口
     * @param dynamicCommentVO
     */
    public void save(DynamicCommentVO dynamicCommentVO);

    /**
     * 获取评论列表
     * @param pageNum
     * @param pageSize
     * @param dynamicId
     * @return
     */
    public PageInfo<DynamicCommentVO> list(Integer pageNum, Integer pageSize, Integer dynamicId);
}
