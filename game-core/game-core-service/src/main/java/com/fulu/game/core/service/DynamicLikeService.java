package com.fulu.game.core.service;

import com.fulu.game.core.entity.DynamicLike;
import com.fulu.game.core.entity.vo.DynamicLikeVO;
import com.github.pagehelper.PageInfo;


/**
 * 动态点赞表
 * 
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-08-30 11:09:38
 */
public interface DynamicLikeService extends ICommonService<DynamicLike,Integer>{

    /**
     * 点赞接口
     * @param dynamicLikeVO
     */
    public void save(DynamicLikeVO dynamicLikeVO);

    /**
     * 获取点赞记录列表接口
     * @param pageNum
     * @param pageSize
     * @param dynamicId
     * @return
     */
    public PageInfo<DynamicLike> list(Integer pageNum, Integer pageSize, Integer dynamicId);
}
