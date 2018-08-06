package com.fulu.game.core.service;

import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.Product;
import com.fulu.game.core.entity.SalesMode;
import com.fulu.game.core.entity.vo.*;
import com.fulu.game.core.entity.vo.ProductDetailsVO;
import com.fulu.game.core.entity.vo.ProductShowCaseVO;
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
     * 保存陪玩师的接单方式
     * @param techAuthId
     * @param price
     * @param unitId
     * @return
     */
    Product save(Integer techAuthId,BigDecimal price,Integer unitId);


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
     * 更新游戏同步更新商品字段
     * @param category
     */
    void updateByCategory(Category category);

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
    Product enable(Product product,boolean status);

    /**
     * 技能下所有商品激活和失效
     * @param techId
     * @param status
     */
    void techEnable(int techId,boolean status);


    /**
     * 通过techId查询商品
     * @param techId
     * @return
     */
    List<Product> findByTechId(int techId);

    /**
     * 通过techId和saleModeId查询商品
     * @param techId
     * @param saleModeId
     * @return
     */
    Product findByTechAndSaleMode(int techId,int saleModeId);

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
     * 更新用户所有商品索引
     * @param userId
     * @param needUpdateTime  是否要更新商品接单时间
     */
    void updateUserProductIndex(Integer userId, Boolean needUpdateTime);

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
    void disabledProductByUser(Integer userId);

    /**
     * 删除技能下所有商品
     * @param techAuthId
     */
    void disabledProductByTech(Integer techAuthId);

    /**
     * 批量更新所有用户索引
     */
    void bathUpdateProductIndex();
    /**
     * 逻辑删除商品
     * @param product
     * @return
     */
    int disabledProduct(Product product);

    /**
     * 恢复商品删除状态
     * @param productId
     */
    void recoverProductActivate(int productId);

    /**
     * 通过TechAuthId恢复商品状态
     * @param techAuthId
     */
    void recoverProductActivateByTechAuthId(Integer techAuthId);

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

    /**
     * 查询技能商品列表
     * @param userId
     * @return
     */
    List<TechAuthProductVO> techAuthProductList(int userId);


}
