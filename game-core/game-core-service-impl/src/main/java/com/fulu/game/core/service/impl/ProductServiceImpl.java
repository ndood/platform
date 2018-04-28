package com.fulu.game.core.service.impl;


import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.ProductDao;
import com.fulu.game.core.entity.Product;
import com.fulu.game.core.entity.TechTag;
import com.fulu.game.core.entity.TechValue;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.ProductVO;
import com.fulu.game.core.entity.vo.ServerCardVO;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.service.*;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class ProductServiceImpl extends AbsCommonService<Product, Integer> implements ProductService {

    @Autowired
    private ProductDao productDao;
    @Autowired
    private UserTechAuthService userTechAuthService;
    @Autowired
    private TechValueService techValueService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private UserInfoAuthService userInfoAuthService;
    @Autowired
    private TechTagService techTagService;

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
        product.setProductName(userTechAuth.getCategoryName());
        product.setTechAuthId(userTechAuth.getId());
        product.setUnitTechValueId(techValue.getId());
        product.setUnit(techValue.getName());
        product.setUnitTechValueRank(techValue.getRank());
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
        if (redisOpenService.hasKey(RedisKeyEnum.PRODUCT_ENABLE_KEY.generateKey(id))) {
            throw new ServiceErrorException("在线技能不允许修改!");
        }
        Product product = findById(id);
        if (techAuthId != null) {
            UserTechAuth userTechAuth = userTechAuthService.findById(techAuthId);
            product.setCategoryId(userTechAuth.getCategoryId());
            product.setProductName(userTechAuth.getCategoryName());
            product.setTechAuthId(userTechAuth.getId());
        }
        if (price != null) {
            product.setPrice(price);
        }
        if (unitId != null) {
            TechValue techValue = techValueService.findById(unitId);
            product.setUnitTechValueId(techValue.getId());
            product.setUnit(techValue.getName());
            product.setUnitTechValueRank(techValue.getRank());
        }
        product.setUpdateTime(new Date());
        update(product);
        return product;
    }


    @Override
    public Product enable(int id, boolean status) {
        if (redisOpenService.hasKey(RedisKeyEnum.PRODUCT_ENABLE_KEY.generateKey(id))) {
            throw new ServiceErrorException("在线技能不允许修改!");
        }
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
    public List<Product> findEnabledProductByUser(int userId) {
        ProductVO productVO = new ProductVO();
        productVO.setStatus(true);
        productVO.setUserId(userId);
        return productDao.findByParameter(productVO);
    }

    /**
     * 开始接单业务
     */
    @Override
    public void startOrderReceiving(int hour) {
        Long expire = hour * 3600L;
        List<Product> products = findEnabledProductByUser(Constant.DEF_USER_ID);
        if (products.isEmpty()) {
            throw new ServiceErrorException("请选择技能后再点击开始接单!");
        }
        redisOpenService.hset(RedisKeyEnum.USER_ORDER_RECEIVE_TIME_KEY.generateKey(Constant.DEF_USER_ID), "HOUR", hour, expire);
        redisOpenService.hset(RedisKeyEnum.USER_ORDER_RECEIVE_TIME_KEY.generateKey(Constant.DEF_USER_ID), "START_TIME", DateUtil.now(), expire);
        for (Product product : products) {
            ProductVO productVO = new ProductVO();
            BeanUtil.copyProperties(product, productVO);
            productVO.setHour(hour);
            productVO.setStartTime(new Date());
            try {
                log.info("开始接单设置{}小时【{}】", hour, productVO);
                redisOpenService.hset(RedisKeyEnum.PRODUCT_ENABLE_KEY.generateKey(product.getId()), BeanUtil.beanToMap(productVO), expire);
            } catch (Exception e) {
                log.error("开始接单设置", e);
                throw new ServiceErrorException("开始接单操作失败!");
            }
        }
    }

    @Override
    public void stopOrderReceiving() {
        List<Product> products = findEnabledProductByUser(Constant.DEF_USER_ID);
        redisOpenService.delete(RedisKeyEnum.USER_ORDER_RECEIVE_TIME_KEY.generateKey(Constant.DEF_USER_ID));
        for (Product product : products) {
            try {
                log.info("停止接单{}", product);
                redisOpenService.delete(RedisKeyEnum.PRODUCT_ENABLE_KEY.generateKey(product.getId()));
            } catch (Exception e) {
                log.error("停止接单设置", e);
                throw new ServiceErrorException("开始接单操作失败!");
            }
        }
    }


    @Override
    public ServerCardVO findByProductId(Integer productId) {
        Product product = findById(productId);
        ServerCardVO.UserInfo userInfo = userInfoAuthService.findUserCardByUserId(product.getUserId());
        List<String> techTags = new ArrayList<>();
        List<TechTag> techTagList = techTagService.findByTechAuthId(product.getTechAuthId());
        for(TechTag techTag : techTagList){
            techTags.add(techTag.getName());
        }
        List<ProductVO> productVOList = findOtherProductVO(product.getUserId(),productId);
        ServerCardVO serverCardVO =ServerCardVO.builder()
                                    .categoryId(product.getCategoryId())
                                    .productId(product.getId())
                                    .productName(product.getProductName())
                                    .categoryIcon(product.getCategoryIcon())
                                    .price(product.getPrice())
                                    .unit(product.getUnit())
                                    .techAuthId(product.getTechAuthId())
                                    .userInfo(userInfo)
                                    .techTags(techTags)
                                    .otherProduct(productVOList)
                                    .build();
        return serverCardVO;
    }



    public List<ProductVO> findOtherProductVO(Integer userId,Integer productId){
        List<Product>  products =   findEnabledProductByUser(userId);
        List<ProductVO> productVOS = new ArrayList<>();
        for(Product product : products){
            if(product.getId().equals(productId)){
                continue;
            }
            ProductVO productVO = new ProductVO();
            BeanUtil.copyProperties(product,productVO);
            List<String> techTags = new ArrayList<>();
            List<TechTag> techTagList = techTagService.findByTechAuthId(productVO.getTechAuthId());
            for(TechTag techTag : techTagList){
                techTags.add(techTag.getName());
            }
            productVO.setTechTags(techTags);
            productVOS.add(productVO);
        }
        return productVOS;
    }





    public Map<String, Object> readOrderReceivingStatus() {
        return redisOpenService.hget(RedisKeyEnum.USER_ORDER_RECEIVE_TIME_KEY.generateKey(Constant.DEF_USER_ID));
    }


}
