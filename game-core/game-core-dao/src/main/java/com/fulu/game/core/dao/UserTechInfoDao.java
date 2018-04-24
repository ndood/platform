package com.fulu.game.core.dao;

import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.UserTechInfo;
import com.fulu.game.core.entity.vo.UserTechInfoVO;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 用户技能具体信息表
 * @author wangbin
 * @email ${email}
 * @date 2018-04-23 11:17:40
 */
@Mapper
public interface UserTechInfoDao extends ICommonDao<UserTechInfo,Integer> {

    List<UserTechInfo> findByParameter(UserTechInfoVO userTechInfoVO);

    int deleteByTechAuthId(Integer techAuthId);
}
