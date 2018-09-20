package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.SalesModeVO;
import com.fulu.game.core.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.SalesModeDao;
import com.fulu.game.core.entity.SalesMode;
import com.fulu.game.core.service.SalesModeService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Service
public class SalesModeServiceImpl extends AbsCommonService<SalesMode,Integer> implements SalesModeService {

    @Autowired
	private SalesModeDao salesModeDao;
    @Autowired
    private ProductService productService;

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

    @Override
    public List<SalesMode> findByCategoryAndPlatformShow(Integer categoryId,List<Integer> platFormList) {
        return salesModeDao.findByCategoryAndPlatformShow(categoryId, platFormList);
    }

    @Override
    public SalesMode update(Integer id, String name, BigDecimal price, Integer rank) {
        SalesMode salesMode = findById(id);
        salesMode.setName(name);
        salesMode.setPrice(price);
        salesMode.setRank(rank);
        salesMode.setUpdateTime(new Date());
        int result =  update(salesMode);
        //更新商品的销售方式
        productService.updateProductSalesModel(salesMode);
        return salesMode;
    }
}
