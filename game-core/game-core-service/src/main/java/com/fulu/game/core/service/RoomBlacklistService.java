package com.fulu.game.core.service;

import com.fulu.game.core.entity.RoomBlacklist;


/**
 * 房间黑名单
 *
 * @author wangbin
 * @email ${email}
 * @date 2018-10-11 17:21:16
 */
public interface RoomBlacklistService extends ICommonService<RoomBlacklist, Integer> {


    void create(Integer userId, String roomNo);

    void delete(Integer userId, String roomNo);

    RoomBlacklist findByUserAndRoomNo(Integer userId,String roomNo);

}
