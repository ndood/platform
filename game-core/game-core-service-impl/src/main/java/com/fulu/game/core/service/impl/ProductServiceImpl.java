package com.fulu.game.core.service.impl;


import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.ProductDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.*;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    @Autowired
    private UserService userService;


    @Override
    public ICommonDao<Product, Integer> getDao() {
        return productDao;
    }

    @Override
    public Product create(Integer techAuthId, BigDecimal price, Integer unitId) {
        UserTechAuth userTechAuth = userTechAuthService.findById(techAuthId);
        User user = userService.findById(userTechAuth.getUserId());
        //查询销售方式的单位
        TechValue techValue = techValueService.findById(unitId);
        Product product = new Product();
        product.setCategoryId(userTechAuth.getCategoryId());
        product.setGender(user.getGender());
        product.setProductName(userTechAuth.getCategoryName());
        product.setDescription(userTechAuth.getDescription());
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
            product.setDescription(userTechAuth.getDescription());
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
        User user =(User) SubjectUtil.getCurrentUser();
        Long expire = hour * 3600L;
        List<Product> products = findEnabledProductByUser(user.getId());
        if (products.isEmpty()) {
            throw new ServiceErrorException("请选择技能后再点击开始接单!");
        }
        redisOpenService.hset(RedisKeyEnum.USER_ORDER_RECEIVE_TIME_KEY.generateKey(user.getId()), "HOUR", hour, expire);
        redisOpenService.hset(RedisKeyEnum.USER_ORDER_RECEIVE_TIME_KEY.generateKey(user.getId()), "START_TIME", DateUtil.now(), expire);
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
        User user =(User) SubjectUtil.getCurrentUser();
        List<Product> products = findEnabledProductByUser(user.getId());
        redisOpenService.delete(RedisKeyEnum.USER_ORDER_RECEIVE_TIME_KEY.generateKey(user.getId()));
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
    public ProductDetailsVO findDetailsByProductId(Integer productId) {
        Product product = findById(productId);
        UserInfoVO userInfo = userInfoAuthService.findUserCardByUserId(product.getUserId(),true,true);
        List<String> techTags = new ArrayList<>();
        List<TechTag> techTagList = techTagService.findByTechAuthId(product.getTechAuthId());
        for(TechTag techTag : techTagList){
            techTags.add(techTag.getName());
        }
        List<ProductVO> productVOList = findOtherProductVO(product.getUserId(),productId);
        ProductDetailsVO serverCardVO = ProductDetailsVO.builder()
                                    .categoryId(product.getCategoryId())
                                    .id(product.getId())
                                    .description(product.getDescription())
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

    @Override
    public List<Product> findByUserId(Integer userId) {
        ProductVO productVO = new ProductVO();
        productVO.setUserId(userId);
        return productDao.findByParameter(productVO);
    }


    @Override
    public PageInfo<ProductShowCaseVO> findProductShowCase(Integer categoryId,
                                                           Integer gender,
                                                           Integer pageNum,
                                                           Integer pageSize,
                                                           String orderBy) {
        if(StringUtils.isBlank(orderBy)){
            orderBy = "create_time desc";
        }
        PageHelper.startPage(pageNum,pageSize,orderBy);
        List<ProductShowCaseVO> showCaseVOS = productDao.findProductShowCase(categoryId,gender);
        for(ProductShowCaseVO showCaseVO : showCaseVOS){
            UserInfoVO userInfoVO = userInfoAuthService.findUserCardByUserId(showCaseVO.getUserId(),false,false);
            showCaseVO.setNickName(userInfoVO.getNickName());
            showCaseVO.setMainPhoto(userInfoVO.getMainPhotoUrl());
            showCaseVO.setCity(userInfoVO.getCity());
            showCaseVO.setPersonTags(userInfoVO.getTags());
        }
        PageInfo page = new PageInfo(showCaseVOS);
        return page;
    }


    /**
     *  判断商品是否是开始接单状态
      * @param productId
     * @return
     */
    @Override
    public Boolean isProductStartOrderReceiving(Integer productId){
        return redisOpenService.hasKey(RedisKeyEnum.PRODUCT_ENABLE_KEY.generateKey(productId));
    }

    /**
     * 查找用户其他的商品
     * @param userId
     * @param productId
     * @return
     */
    public List<ProductVO> findOtherProductVO(Integer userId,Integer productId){
        List<Product>  products =   findByUserId(userId);
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


    @Override
    public Boolean isUserStartOrderReceiving(Integer userId){
        return redisOpenService.hasKey( RedisKeyEnum.USER_ORDER_RECEIVE_TIME_KEY.generateKey(userId));
    }


    public Map<String, Object> readOrderReceivingStatus() {
        User user =(User) SubjectUtil.getCurrentUser();
        return redisOpenService.hget(RedisKeyEnum.USER_ORDER_RECEIVE_TIME_KEY.generateKey(user.getId()));
    }


}
