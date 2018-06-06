package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.PushMsgDao;
import com.fulu.game.core.entity.PushMsg;
import com.fulu.game.core.service.PushMsgService;



@Service
public class PushMsgServiceImpl extends AbsCommonService<PushMsg,Integer> implements PushMsgService {

    @Autowired
	private PushMsgDao pushMsgDao;



    @Override
    public ICommonDao<PushMsg, Integer> getDao() {
        return pushMsgDao;
    }
	
}
