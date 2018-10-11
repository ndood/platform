package com.fulu.game.core.dao;

import com.fulu.game.core.entity.RoomBlacklist;
import com.fulu.game.core.entity.vo.RoomBlacklistVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 房间黑名单
 * @author wangbin
 * @email ${email}
 * @date 2018-10-11 17:21:16
 */
@Mapper
public interface RoomBlacklistDao extends ICommonDao<RoomBlacklist,Integer>{

    List<RoomBlacklist> findByParameter(RoomBlacklistVO roomBlacklistVO);

}
