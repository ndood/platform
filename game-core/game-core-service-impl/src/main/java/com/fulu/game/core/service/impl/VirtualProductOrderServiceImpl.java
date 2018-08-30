package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualProductOrderDao;
import com.fulu.game.core.entity.VirtualProductOrder;
import com.fulu.game.core.service.VirtualProductOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class VirtualProductOrderServiceImpl extends AbsCommonService<VirtualProductOrder, Integer> implements VirtualProductOrderService {

    @Autowired
    private VirtualProductOrderDao virtualProductOrderDao;


    @Override
    public ICommonDao<VirtualProductOrder, Integer> getDao() {
        return virtualProductOrderDao;
    }

}
