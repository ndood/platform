package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.ProductTopDao;
import com.fulu.game.core.entity.ProductTop;
import com.fulu.game.core.entity.vo.ProductTopVO;
import com.fulu.game.core.service.ProductService;
import com.fulu.game.core.service.ProductTopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class ProductTopServiceImpl extends AbsCommonService<ProductTop, Integer> implements ProductTopService {

    @Autowired
    private ProductTopDao productTopDao;
    @Autowired
    private ProductService productService;

    @Override
    public ICommonDao<ProductTop, Integer> getDao() {
        return productTopDao;
    }


    @Override
    public void productTopPutAway(Integer id, Boolean status) {
        ProductTop productTop = findById(id);
        if (status) {
            productTop.setStatus(status);
            productTop.setUpdateTime(new Date());
            update(productTop);
        } else {
            productTop.setStatus(status);
            productTop.setUpdateTime(new Date());
            update(productTop);
        }
        productService.updateUserProductIndex(productTop.getUserId(),Boolean.FALSE);
    }

    @Override
    public int findTopSortByUserCategory(Integer userId, Integer categoryId) {
        ProductTopVO param = new ProductTopVO();
        param.setUserId(userId);
        param.setCategoryId(categoryId);
        param.setStatus(Boolean.TRUE);
        List<ProductTop> productTopList =  productTopDao.findByParameter(param);
        if(productTopList.isEmpty()){
            return 0;
        }
        return productTopList.get(0).getSort();
    }


}
