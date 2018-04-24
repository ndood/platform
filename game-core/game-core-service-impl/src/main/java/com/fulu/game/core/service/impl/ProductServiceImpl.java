package com.fulu.game.core.service.impl;


import com.fulu.game.common.Constant;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.ProductVO;
import com.fulu.game.core.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.ProductDao;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Service
public class ProductServiceImpl extends AbsCommonService<Product,Integer> implements ProductService {

    @Autowired
	private ProductDao productDao;
    @Autowired
    private UserTechAuthService userTechAuthService;
    @Autowired
    private TechValueService techValueService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;

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

    @Override
    public Product update(Integer id, Integer techAuthId, BigDecimal price, Integer unitId) {
        Product product = findById(id);
        //todo 判断商品上架状态是否在redis存在,存在不能修改
        if(techAuthId!=null){
            UserTechAuth userTechAuth = userTechAuthService.findById(techAuthId);
            product.setCategoryId(userTechAuth.getCategoryId());
            product.setCategoryName(userTechAuth.getCategoryName());
            product.setTechAuthId(userTechAuth.getId());
        }
        if(price==null){
            product.setPrice(price);
        }
        if(unitId==null){
            TechValue techValue = techValueService.findById(unitId);
            product.setUnitTechValueId(techValue.getId());
            product.setUnit(techValue.getName());
        }
        product.setUpdateTime(new Date());
        update(product);
        return product;
    }


    @Override
    public Product enable(int id,boolean status) {
        //todo 判断商品上架状态是否在redis存在,存在不能修改
        Product product = findById(id);
        product.setStatus(status);
        update(product);
        return product;
    }

    /**
     * 查找激活的商品
     * @param userId
     * @return
     */
    public List<Product> findEnableProductByUser(int userId){
        ProductVO productVO = new ProductVO();
        productVO.setStatus(true);
        return productDao.findByParameter(productVO);
    }

    /**
     * 开始接单业务
     */
    public void startOrderReceiving(){
        List<Product>  products =  findEnableProductByUser(Constant.DEF_USER_ID);
    }


}
