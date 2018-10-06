package com.fulu.game.core.dao;

import com.fulu.game.core.entity.RoomManage;
import com.fulu.game.core.entity.vo.RoomManageVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 房间管理表
 * @author wangbin
 * @email ${email}
 * @date 2018-10-07 00:25:52
 */
@Mapper
public interface RoomManageDao extends ICommonDao<RoomManage,Integer>{

    List<RoomManage> findByParameter(RoomManageVO roomManageVO);

}
