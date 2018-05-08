package com.fulu.game.core.service;

import com.fulu.game.core.entity.Product;
import com.fulu.game.core.entity.vo.ProductShowCaseVO;
import com.fulu.game.core.entity.vo.ProductDetailsVO;
import com.fulu.game.core.entity.vo.SimpleProductVO;
import com.github.pagehelper.PageInfo;

import java.math.BigDecimal;
import java.util.List;
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
    void startOrderReceiving(Float hour);

    /**
     * 停止接单
     */
    void stopOrderReceiving();

    /**
     * 判断商品是否是开始接单状态
     * @param productId
     * @return
     */
    Boolean isProductStartOrderReceivingStatus(Integer productId);


    /**
     * 判断陪玩师是否是开始接单状态
     * @param userId
     * @return
     */
    Boolean isUserStartOrderReceivingStatus(Integer userId);


    /**
     * 查询商品详情页
     * @param productId
     * @return
     */
    ProductDetailsVO findDetailsByProductId(Integer productId);


    SimpleProductVO findSimpleProductByProductId(Integer productId);

    /**
     * 查询用户所有商品
     * @param userId
     * @return
     */
    List<Product> findByUserId(Integer userId);

    /**
     * 查询商品橱窗
     * @param pageNum
     * @param pageSize
     * @param categoryId
     * @return
     */
    PageInfo<ProductShowCaseVO> findProductShowCase(Integer categoryId,Integer gender,Integer pageNum,Integer pageSize,String orderBy);
}
