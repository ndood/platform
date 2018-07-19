package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.ArbitrationDetailsDao;
import com.fulu.game.core.entity.ArbitrationDetails;
import com.fulu.game.core.service.ArbitrationDetailsService;



@Service
public class ArbitrationDetailsServiceImpl extends AbsCommonService<ArbitrationDetails,Integer> implements ArbitrationDetailsService {

    @Autowired
	private ArbitrationDetailsDao arbitrationDetailsDao;



    @Override
    public ICommonDao<ArbitrationDetails, Integer> getDao() {
        return arbitrationDetailsDao;
    }
	
}
