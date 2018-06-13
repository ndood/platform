package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Channel;
import com.fulu.game.core.entity.vo.ChannelVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 渠道商表
 *
 * @author yanbiao
 * @date 2018-06-13 15:33:34
 */
@Mapper
public interface ChannelDao extends ICommonDao<Channel, Integer> {

    List<Channel> findByParameter(ChannelVO channelVO);

}
