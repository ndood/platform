package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.AccessLogDetailDao;
import com.fulu.game.core.entity.AccessLogDetail;
import com.fulu.game.core.service.AccessLogDetailService;



@Service("accessLogDetailService")
public class AccessLogDetailServiceImpl extends AbsCommonService<AccessLogDetail,Long> implements AccessLogDetailService {

    @Autowired
	private AccessLogDetailDao accessLogDetailDao;



    @Override
    public ICommonDao<AccessLogDetail, Long> getDao() {
        return accessLogDetailDao;
    }
	
}
