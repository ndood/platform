package com.fulu.game.core.service.impl;

import com.fulu.game.core.dao.CdkGroupDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.CdkGroup;
import com.fulu.game.core.service.CdkGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CdkGroupServiceImpl extends AbsCommonService<CdkGroup, Integer> implements CdkGroupService {

    @Autowired
    private CdkGroupDao cdkGroupDao;

    @Override
    public ICommonDao<CdkGroup, Integer> getDao() {
        return cdkGroupDao;
    }

}
