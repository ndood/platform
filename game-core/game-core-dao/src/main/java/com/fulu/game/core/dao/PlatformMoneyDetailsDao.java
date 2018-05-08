package com.fulu.game.core.dao;

import com.fulu.game.core.entity.PlatformMoneyDetails;
import com.fulu.game.core.entity.vo.PlatformMoneyDetailsVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 平台流水表
 * @author wangbin
 * @email ${email}
 * @date 2018-05-02 17:10:26
 */
@Mapper
public interface PlatformMoneyDetailsDao extends ICommonDao<PlatformMoneyDetails,Integer>{

    List<PlatformMoneyDetails> findByParameter(PlatformMoneyDetailsVO platformMoneyDetailsVO);


    PlatformMoneyDetails findLastMoneyDetails();
}
