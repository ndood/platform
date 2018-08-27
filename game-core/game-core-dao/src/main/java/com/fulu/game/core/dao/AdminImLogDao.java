package com.fulu.game.core.dao;

import com.fulu.game.core.entity.AdminImLog;
import com.fulu.game.core.entity.vo.AdminImLogVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author jaycee Deng
 * @email ${email}
 * @date 2018-08-27 17:54:37
 */
@Mapper
public interface AdminImLogDao extends ICommonDao<AdminImLog,Integer>{

    List<AdminImLog> findByParameter(AdminImLogVO adminImLogVO);

    int deleteByUserId(int userId);

    List<AdminImLog> findByUserId(int UserId);
}
