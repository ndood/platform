package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.WechatFormidDao;
import com.fulu.game.core.entity.WechatFormid;
import com.fulu.game.core.service.WechatFormidService;



@Service
public class WechatFormidServiceImpl extends AbsCommonService<WechatFormid,Integer> implements WechatFormidService {

    @Autowired
	private WechatFormidDao wechatFormidDao;



    @Override
    public ICommonDao<WechatFormid, Integer> getDao() {
        return wechatFormidDao;
    }
	
}
