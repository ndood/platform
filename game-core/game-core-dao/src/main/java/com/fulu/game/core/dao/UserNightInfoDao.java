package com.fulu.game.core.dao;

import com.fulu.game.core.entity.UserNightInfo;
import com.fulu.game.core.entity.vo.UserNightInfoVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 午夜场陪玩师信息表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-09-17 15:32:26
 */
@Mapper
public interface UserNightInfoDao extends ICommonDao<UserNightInfo, Integer> {

    List<UserNightInfo> findByParameter(UserNightInfoVO userNightInfoVO);

    List<UserNightInfo> list(UserNightInfoVO userNightInfoVO);
}
