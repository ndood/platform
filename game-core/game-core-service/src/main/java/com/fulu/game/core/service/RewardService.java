package com.fulu.game.core.service;

import com.fulu.game.core.entity.Reward;
import com.fulu.game.core.entity.vo.RewardVO;
import com.github.pagehelper.PageInfo;


/**
 * 打赏记录表
 * 
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-08-30 11:14:29
 */
public interface RewardService extends ICommonService<Reward,Integer>{

    /**
     * 保存打赏记录
     * @param rewardVO
     * @return
     */
    public void save(RewardVO rewardVO);

    /**
     * 获取打赏记录列表
     * @param pageNum
     * @param pageSize
     * @param resourceId   打赏来源id
     * @param resourceType 打赏来源类型（1：动态打赏）
     * @return
     */
    public PageInfo<Reward> list(Integer pageNum, Integer pageSize, Integer resourceId, Integer resourceType);
}
