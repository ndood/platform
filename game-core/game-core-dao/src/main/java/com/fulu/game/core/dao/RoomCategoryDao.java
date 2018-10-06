package com.fulu.game.core.dao;

import com.fulu.game.core.entity.RoomCategory;
import com.fulu.game.core.entity.vo.RoomCategoryVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 房间分类表
 * @author wangbin
 * @email ${email}
 * @date 2018-10-07 00:25:52
 */
@Mapper
public interface RoomCategoryDao extends ICommonDao<RoomCategory,Integer>{

    List<RoomCategory> findByParameter(RoomCategoryVO roomCategoryVO);

}
