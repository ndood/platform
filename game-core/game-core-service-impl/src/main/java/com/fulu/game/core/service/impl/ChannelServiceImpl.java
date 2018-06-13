package com.fulu.game.core.service.impl;

import com.fulu.game.core.dao.ChannelDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Channel;
import com.fulu.game.core.service.ChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChannelServiceImpl extends AbsCommonService<Channel, Integer> implements ChannelService {

    @Autowired
    private ChannelDao channelDao;

    @Override
    public ICommonDao<Channel, Integer> getDao() {
        return channelDao;
    }

}
