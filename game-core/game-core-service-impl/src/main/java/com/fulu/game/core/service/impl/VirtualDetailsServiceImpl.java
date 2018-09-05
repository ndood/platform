package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualDetailsDao;
import com.fulu.game.core.entity.VirtualDetails;
import com.fulu.game.core.service.VirtualDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class VirtualDetailsServiceImpl extends AbsCommonService<VirtualDetails, Integer> implements VirtualDetailsService {

    @Autowired
    private VirtualDetailsDao virtualDetailsDao;

    @Override
    public ICommonDao<VirtualDetails, Integer> getDao() {
        return virtualDetailsDao;
    }
}
