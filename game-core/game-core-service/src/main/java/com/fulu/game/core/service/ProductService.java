package com.fulu.game.core.service;

import com.fulu.game.core.entity.Product;

import java.math.BigDecimal;
import java.util.Map;


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
     * @param unitId
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

    /**
     * 查看用户开始接单状态
     * @return
     */
    Map<String,Object> readOrderReceivingStatus();

    /**
     * 订单状态修改
     * @param id
     * @param status
     * @return
     */
    Product enable(int id,boolean status);

    /**
     * 开始接单
     * @param hour
     */
    void startOrderReceiving(int hour);

    /**
     * 停止接单
     */
    void stopOrderReceiving();
}
