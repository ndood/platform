package com.fulu.game.core.dao;

import com.fulu.game.core.entity.ChannelCashDetails;
import com.fulu.game.core.entity.vo.ChannelCashDetailsVO;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * 渠道商金额流水表
 *
 * @author yanbiao
 * @date 2018-06-13 15:33:45
 */
@Mapper
public interface ChannelCashDetailsDao extends ICommonDao<ChannelCashDetails, Integer> {

    List<ChannelCashDetails> findByParameter(ChannelCashDetailsVO channelCashDetailsVO);

    BigDecimal sumByChannelId(Integer channelId);
}
