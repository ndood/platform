package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.GiftConfigDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.GiftConfig;
import com.fulu.game.core.service.GiftConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class GiftConfigServiceImpl extends AbsCommonService<GiftConfig, Integer> implements GiftConfigService {

    @Autowired
    private GiftConfigDao giftConfigDao;


    @Override
    public ICommonDao<GiftConfig, Integer> getDao() {
        return giftConfigDao;
    }

}
