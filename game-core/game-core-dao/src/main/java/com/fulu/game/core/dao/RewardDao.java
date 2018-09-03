package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Reward;
import com.fulu.game.core.entity.vo.RewardVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 打赏记录表
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-08-30 11:14:29
 */
@Mapper
public interface RewardDao extends ICommonDao<Reward,Long>{

    List<Reward> findByParameter(RewardVO rewardVO);

}
