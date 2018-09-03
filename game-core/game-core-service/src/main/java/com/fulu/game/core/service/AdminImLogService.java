package com.fulu.game.core.service;

import com.fulu.game.core.entity.AdminImLog;
import com.fulu.game.core.entity.vo.AdminImLogVO;

import java.util.List;


/**
 * 
 * 
 * @author jaycee Deng
 * @email ${email}
 * @date 2018-08-27 17:54:37
 */
public interface AdminImLogService extends ICommonService<AdminImLog,Integer>{
    
    List<AdminImLog> findByImId(String imId);

    int deleteByImId(String imId);

    int deleteByOwnerUserId(Integer ownerUserId);

    List<AdminImLog> findByParameter(AdminImLogVO adminImLogVO);
}
