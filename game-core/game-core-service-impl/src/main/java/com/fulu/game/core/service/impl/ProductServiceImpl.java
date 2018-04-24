package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.ProductDao;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;


@Service
public class ProductServiceImpl extends AbsCommonService<Product,Integer> implements ProductService {

    @Autowired
	private ProductDao productDao;
    @Autowired
    private UserTechAuthService userTechAuthService;
    @Autowired
    private TechValueService techValueService;


    @Override
    public ICommonDao<Product, Integer> getDao() {
        return productDao;
    }


    @Override
    public Product create(Integer techAuthId, BigDecimal price, Integer unitId) {
        UserTechAuth userTechAuth = userTechAuthService.findById(techAuthId);
        //查询销售方式的单位
        TechValue techValue = techValueService.findById(unitId);
        Product product = new Product();
        product.setCategoryId(userTechAuth.getCategoryId());
        product.setCategoryName(userTechAuth.getCategoryName());
        product.setTechAuthId(userTechAuth.getId());
        product.setUnitTechValueId(techValue.getId());
        product.setUnit(techValue.getName());
        product.setUserId(userTechAuth.getUserId());
        product.setPrice(price);
        product.setStatus(false);
        product.setCreateTime(new Date());
        product.setUpdateTime(new Date());
        create(product);
        return product;
    }


}
