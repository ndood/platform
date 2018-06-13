package com.fulu.game.core.service.impl;

import com.fulu.game.core.dao.ChannelCashDetailsDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.ChannelCashDetails;
import com.fulu.game.core.service.ChannelCashDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChannelCashDetailsServiceImpl extends AbsCommonService<ChannelCashDetails, Integer> implements ChannelCashDetailsService {

    @Autowired
    private ChannelCashDetailsDao channelCashDetailsDao;

    @Override
    public ICommonDao<ChannelCashDetails, Integer> getDao() {
        return channelCashDetailsDao;
    }

}
