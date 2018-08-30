package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualProductDao;
import com.fulu.game.core.entity.VirtualProduct;
import com.fulu.game.core.service.VirtualProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class VirtualProductServiceImpl extends AbsCommonService<VirtualProduct, Integer> implements VirtualProductService {

    @Autowired
    private VirtualProductDao virtualProductDao;


    @Override
    public ICommonDao<VirtualProduct, Integer> getDao() {
        return virtualProductDao;
    }

}
