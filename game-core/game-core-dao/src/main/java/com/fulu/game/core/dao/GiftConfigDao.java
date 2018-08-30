package com.fulu.game.core.dao;

import com.fulu.game.core.entity.GiftConfig;
import com.fulu.game.core.entity.vo.GiftConfigVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 礼物配置表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-30 11:35:12
 */
@Mapper
public interface GiftConfigDao extends ICommonDao<GiftConfig, Integer> {

    List<GiftConfig> findByParameter(GiftConfigVO giftConfigVO);

}
