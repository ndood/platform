package com.fulu.game.core.service;

import com.fulu.game.core.entity.Channel;
import com.fulu.game.core.entity.ChannelCashDetails;
import com.fulu.game.core.entity.vo.ChannelVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 渠道商表
 *
 * @author yanbiao
 * @date 2018-06-13 15:33:34
 */
public interface ChannelService extends ICommonService<Channel, Integer> {

    /**
     * 参数查询
     *
     * @param channelVO
     * @return
     */
    List<Channel> findByParam(ChannelVO channelVO);

    /**
     * 渠道新增
     *
     * @param name
     * @return
     */
    Channel save(String name);

    /**
     * 修改渠道商名
     *
     * @param id
     * @param name
     * @return
     */
    Channel update(Integer id, String name);

    /**
     * token更新
     *
     * @param id
     */
    String recreate(Integer id);

}
