package com.fulu.game.core.dao;

import com.fulu.game.core.entity.OfficialActivity;
import com.fulu.game.core.entity.vo.OfficialActivityVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 官方公告
 * @author wangbin
 * @email ${email}
 * @date 2018-09-28 14:32:06
 */
@Mapper
public interface OfficialActivityDao extends ICommonDao<OfficialActivity,Integer>{


    List<OfficialActivity> findByParameter(OfficialActivityVO officialActivityVO);




}
