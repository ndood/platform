package com.fulu.game.core.service;

import com.fulu.game.core.entity.Product;

import java.math.BigDecimal;


/**
 * 商品表
 * 
 * @author yanbiao
 * @email ${email}
 * @date 2018-04-24 15:23:43
 */
public interface ProductService extends ICommonService<Product,Integer>{

    /**
     * 陪玩师新建接单方式
     * @param techAuthId
     * @param price
     * @param unit
     * @return
     */
    Product create(Integer techAuthId,BigDecimal price,Integer unitId);

    /**
     * 修改接单方式
     * @param id
     * @param techAuthId
     * @param price
     * @param unitId
     * @return
     */
    Product update(Integer id,Integer techAuthId,BigDecimal price,Integer unitId);


    public Product enable(int id,boolean status);

}
