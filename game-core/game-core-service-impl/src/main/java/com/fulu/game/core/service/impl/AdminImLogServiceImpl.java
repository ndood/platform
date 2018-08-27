package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.AdminImLogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.AdminImLogDao;
import com.fulu.game.core.entity.AdminImLog;
import com.fulu.game.core.service.AdminImLogService;

import java.util.List;


@Service
public class AdminImLogServiceImpl extends AbsCommonService<AdminImLog,Integer> implements AdminImLogService {

    @Autowired
	private AdminImLogDao adminImLogDao;



    @Override
    public ICommonDao<AdminImLog, Integer> getDao() {
        
        return adminImLogDao;
    }

    @Override
    public List<AdminImLog> findByUserId(int userId){
        return adminImLogDao.findByUserId(userId);
    }

    @Override
    public int deleteByUserId(int userId){
        return adminImLogDao.deleteByUserId(userId);
    }
	
}
