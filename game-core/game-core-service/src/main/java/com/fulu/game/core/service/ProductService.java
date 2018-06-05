package com.fulu.game.core.service;

import com.fulu.game.core.entity.Product;
import com.fulu.game.core.entity.SalesMode;
import com.fulu.game.core.entity.vo.ProductShowCaseVO;
import com.fulu.game.core.entity.vo.ProductDetailsVO;
import com.fulu.game.core.entity.vo.ProductVO;
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
     * 为用户所有商品添加索引
     * @param userId
     */
    void batchCreateUserProduct(Integer userId,Boolean updateTime);

    /**
     * 查询商品详情页
     * @param productId
     * @return
     */
    ProductDetailsVO findDetailsByProductId(Integer productId);

    /**
     * 再来一单商品页面
     * @param productId
     * @return
     */
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

    /**
     * 通过昵称查找商品
     * @param pageNum
     * @param pageSize
     * @param nickName
     * @return
     */
    PageInfo searchContent(int pageNum, int pageSize, String nickName);

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
     * 删除用户下所有商品
     * @param userId
     */
    void deleteProductByUser(Integer userId);

    /**
     * 删除技能下所有商品
     * @param techAuthId
     */
    void deleteProductByTech(Integer techAuthId);

    /**
     * 批量更新所有用户索引
     */
    void bathUpdateProductIndex();
    /**
     * 逻辑删除商品
     * @param product
     * @return
     */
    int deleteProduct(Product product);

    /**
     * 恢复商品删除状态
     * @param productId
     */
    void recoverProductDelFlag(int productId);

    /**
     * 通过TechAuthId恢复商品状态
     * @param techAuthId
     */
    void recoverProductDelFlagByTechAuthId(Integer techAuthId);

    /**
     * 修改商品销售方式
     * @param salesMode
     * @return
     */
    int updateProductSalesModel(SalesMode salesMode);

    /**
     * 查询同一种游戏下的所有商品
     */
    List<ProductVO> findOthersByproductId(Integer productId);



}
