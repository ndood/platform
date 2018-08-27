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
    
    List<AdminImLog> findByUserId(int userId);

    int deleteByUserId(int userId);
    
}
