package com.fulu.game.core.service.impl;

import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.RegistSourceDao;
import com.fulu.game.core.entity.RegistSource;
import com.fulu.game.core.service.RegistSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistSourceServiceImpl extends AbsCommonService<RegistSource, Integer> implements RegistSourceService {

    @Autowired
    private RegistSourceDao registSourceDao;

    @Override
    public ICommonDao<RegistSource, Integer> getDao() {
        return registSourceDao;
    }

}
