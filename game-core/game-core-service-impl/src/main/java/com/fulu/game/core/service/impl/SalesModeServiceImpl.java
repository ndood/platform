package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.SalesModeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.SalesModeDao;
import com.fulu.game.core.entity.SalesMode;
import com.fulu.game.core.service.SalesModeService;

import java.util.List;


@Service
public class SalesModeServiceImpl extends AbsCommonService<SalesMode,Integer> implements SalesModeService {

    @Autowired
	private SalesModeDao salesModeDao;



    @Override
    public ICommonDao<SalesMode, Integer> getDao() {
        return salesModeDao;
    }

    @Override
    public List<SalesMode> findByCategory(Integer categoryId) {
        SalesModeVO salesModeVO = new SalesModeVO();
        salesModeVO.setCategoryId(categoryId);
        return salesModeDao.findByParameter(salesModeVO);
    }
}
