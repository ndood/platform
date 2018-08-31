package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualProductDao;
import com.fulu.game.core.entity.VirtualProduct;
import com.fulu.game.core.entity.vo.VirtualProductVO;
import com.fulu.game.core.service.VirtualProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class VirtualProductServiceImpl extends AbsCommonService<VirtualProduct, Integer> implements VirtualProductService {

    @Autowired
    private VirtualProductDao virtualProductDao;


    @Override
    public ICommonDao<VirtualProduct, Integer> getDao() {
        return virtualProductDao;
    }

    @Override
    public List<VirtualProductVO> searchByvirtualProductVo(VirtualProductVO vpo) {

        List<VirtualProductVO> list = null;
                
        try {

           list =  virtualProductDao.findByVirtualProductVo(vpo);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
