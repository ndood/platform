package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.FenqileReconciliationDao;
import com.fulu.game.core.entity.FenqileReconciliation;
import com.fulu.game.core.service.FenqileReconciliationService;



@Service
public class FenqileReconciliationServiceImpl extends AbsCommonService<FenqileReconciliation,Integer> implements FenqileReconciliationService {

    @Autowired
	private FenqileReconciliationDao fenqileReconciliationDao;



    @Override
    public ICommonDao<FenqileReconciliation, Integer> getDao() {
        return fenqileReconciliationDao;
    }
	
}
