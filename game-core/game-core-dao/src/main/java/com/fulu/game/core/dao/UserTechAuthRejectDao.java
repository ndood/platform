package com.fulu.game.core.dao;

import com.fulu.game.core.entity.UserTechAuthReject;
import com.fulu.game.core.entity.vo.UserTechAuthRejectVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 技能认证信息驳回表
 * @author wangbin
 * @email ${email}
 * @date 2018-05-28 18:07:18
 */
@Mapper
public interface UserTechAuthRejectDao extends ICommonDao<UserTechAuthReject,Integer>{

    List<UserTechAuthReject> findByParameter(UserTechAuthRejectVO userTechAuthRejectVO);

}
