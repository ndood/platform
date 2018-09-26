package com.fulu.game.core.dao;

import com.fulu.game.core.entity.AssignOrderSetting;
import com.fulu.game.core.entity.vo.AssignOrderSettingVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 派单设置
 * @author wangbin
 * @email ${email}
 * @date 2018-09-20 18:43:50
 */
@Mapper
public interface AssignOrderSettingDao extends ICommonDao<AssignOrderSetting,Integer>{

    List<AssignOrderSetting> findByParameter(AssignOrderSettingVO assignOrderSettingVO);

}
