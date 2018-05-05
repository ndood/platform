package com.fulu.game.core.service.impl;


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
import com.xiaoleilu.hutool.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.rmi.ServerError;
import java.rmi.ServerException;
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
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private UserInfoAuthService userInfoAuthService;
    @Autowired
    private TechTagService techTagService;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SalesModeService salesModeService;

    @Override
    public ICommonDao<Product, Integer> getDao() {
        return productDao;
    }

    @Override
    public Product create(Integer techAuthId, BigDecimal price, Integer unitId) {
        UserTechAuth userTechAuth = userTechAuthService.findById(techAuthId);
        if(userTechAuth==null){
            throw new ServiceErrorException("不能设置该技能接单!");
        }
        User user = userService.findById(userTechAuth.getUserId());
        userService.isCurrentUser(user.getId());

        //查询销售方式的单位
        SalesMode salesMode = salesModeService.findById(unitId);
        Category category = categoryService.findById(userTechAuth.getCategoryId());
        if(!salesMode.getCategoryId().equals(category.getId())){
            throw new ServiceErrorException("接单方式单位不匹配!");
        }
        List<Product> products = findProductByUserAndSalesMode(user.getId(),userTechAuth.getId(),unitId);
        if(products.size()>0){
            throw new ServiceErrorException("不能设置同样单位的技能!");
        }
        Product product = new Product();
        product.setCategoryId(userTechAuth.getCategoryId());
        product.setGender(user.getGender());
        product.setCategoryIcon(category.getIcon());
        product.setProductName(userTechAuth.getCategoryName());
        product.setDescription(userTechAuth.getDescription());
        product.setTechAuthId(userTechAuth.getId());
        product.setSalesModeId(salesMode.getId());
        product.setUnit(salesMode.getName());
        product.setSalesModeRank(salesMode.getRank()==null?0:salesMode.getRank());
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
        userService.isCurrentUser(product.getUserId());
        if (techAuthId != null) {
            if(!product.getTechAuthId().equals(techAuthId)){
                List<Product> products = findProductByUserAndSalesMode(product.getUserId(),techAuthId,unitId);
                if(products.size()>0){
                    throw new ServiceErrorException("不能设置同样单位的技能!");
                }
            }
            UserTechAuth userTechAuth = userTechAuthService.findById(techAuthId);
            userService.isCurrentUser(userTechAuth.getUserId());
            Category category = categoryService.findById(userTechAuth.getCategoryId());
            product.setCategoryId(userTechAuth.getCategoryId());
            product.setProductName(userTechAuth.getCategoryName());
            product.setDescription(userTechAuth.getDescription());
            product.setTechAuthId(userTechAuth.getId());
            product.setCategoryIcon(category.getIcon());
        }
        if (price != null) {
            product.setPrice(price);
        }
        if (unitId != null) {
            if(!product.getSalesModeId().equals(unitId)){
                List<Product> products = findProductByUserAndSalesMode(product.getUserId(),techAuthId,unitId);
                if(products.size()>0){
                    throw new ServiceErrorException("不能设置同样单位的技能!");
                }
            }
            SalesMode salesMode = salesModeService.findById(unitId);
            product.setSalesModeId(salesMode.getId());
            product.setUnit(salesMode.getName());
            product.setSalesModeRank(salesMode.getRank());
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
     * 查询用户是否设置了同样单位的商品
     * @param userId
     * @param salesModeId
     * @return
     */
    public List<Product> findProductByUserAndSalesMode(int userId,int techAuthId,int salesModeId) {
        ProductVO productVO = new ProductVO();
        productVO.setTechAuthId(techAuthId);
        productVO.setUserId(userId);
        productVO.setSalesModeId(salesModeId);
        return productDao.findByParameter(productVO);
    }


    /**
     * 开始接单业务
     */
    @Override
    public void startOrderReceiving(Float hour) {
        User user =(User) SubjectUtil.getCurrentUser();
        Long expire = (long)(hour * 3600) ;
        List<Product> products = findEnabledProductByUser(user.getId());
        if (products.isEmpty()) {
            throw new ServiceErrorException("请选择技能后再点击开始接单!");
        }
        redisOpenService.hset(RedisKeyEnum.USER_ORDER_RECEIVE_TIME_KEY.generateKey(user.getId()), "HOUR", hour, expire);
        redisOpenService.hset(RedisKeyEnum.USER_ORDER_RECEIVE_TIME_KEY.generateKey(user.getId()), "START_TIME",  new Date().getTime(), expire);
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

    /**
     * 查询用户详情页
     * @param productId
     * @return
     */
    @Override
    public ProductDetailsVO findDetailsByProductId(Integer productId) {
        Product product = findById(productId);
        UserInfoVO userInfo = userInfoAuthService.findUserCardByUserId(product.getUserId(),true,true,false,false);
        List<String> techTags = new ArrayList<>();
        List<TechTag> techTagList = techTagService.findByTechAuthId(product.getTechAuthId());
        for(TechTag techTag : techTagList){
            techTags.add(techTag.getName());
        }
        List<ProductVO> productVOList = findOtherProductVO(product.getUserId(),productId);
        //查询完成订单数
        int orderCount =  orderService.allOrderCount(userInfo.getUserId());
        ProductDetailsVO serverCardVO = ProductDetailsVO.builder()
                                    .categoryId(product.getCategoryId())
                                    .id(product.getId())
                                    .onLine(isProductStartOrderReceivingStatus(product.getId()))
                                    .description(product.getDescription())
                                    .productName(product.getProductName())
                                    .categoryIcon(product.getCategoryIcon())
                                    .price(product.getPrice())
                                    .unit(product.getUnit())
                                    .techAuthId(product.getTechAuthId())
                                    .userInfo(userInfo)
                                    .orderCount(orderCount)
                                    .techTags(techTags)
                                    .otherProduct(productVOList)
                                    .build();
        return serverCardVO;
    }

    /**
     * 查询用户全部的商品
     * @param userId
     * @return
     */
    @Override
    public List<Product> findByUserId(Integer userId) {
        ProductVO productVO = new ProductVO();
        productVO.setUserId(userId);
        return productDao.findByParameter(productVO);
    }

    /**
     * 商品首页和列表页
     * @param categoryId
     * @param gender
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
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
            UserInfoVO userInfoVO = userInfoAuthService.findUserCardByUserId(showCaseVO.getUserId(),false,false,false,false);
            showCaseVO.setNickName(userInfoVO.getNickName());
            showCaseVO.setGender(userInfoVO.getGender());
            showCaseVO.setMainPhoto(userInfoVO.getMainPhotoUrl());
            showCaseVO.setCity(userInfoVO.getCity());
            showCaseVO.setPersonTags(userInfoVO.getTags());
            showCaseVO.setOnLine(isProductStartOrderReceivingStatus(showCaseVO.getId()));
        }
        PageInfo page = new PageInfo(showCaseVOS);
        return page;
    }


    /**
     * 查找用户其他的商品
     * @param userId
     * @param productId
     * @return
     */
    public List<ProductVO> findOtherProductVO(Integer userId,Integer productId){
        List<Product>  products = findByUserId(userId);
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
            if(isUserStartOrderReceivingStatus(userId)){
                if(isProductStartOrderReceivingStatus(productVO.getId())){
                    productVO.setOnLine(true);
                    productVO.setTechTags(techTags);
                    productVOS.add(productVO);
                }
            }else{
                productVO.setOnLine(isProductStartOrderReceivingStatus(productVO.getId()));
                productVO.setTechTags(techTags);
                productVOS.add(productVO);
            }
        }
        return productVOS;
    }

    /**
     *  判断商品是否是开始接单状态
     * @param productId
     * @return
     */
    @Override
    public Boolean isProductStartOrderReceivingStatus(Integer productId){
        return redisOpenService.hasKey(RedisKeyEnum.PRODUCT_ENABLE_KEY.generateKey(productId));
    }


    @Override
    public Boolean isUserStartOrderReceivingStatus(Integer userId){
        return redisOpenService.hasKey( RedisKeyEnum.USER_ORDER_RECEIVE_TIME_KEY.generateKey(userId));
    }


    public Map<String, Object> readOrderReceivingStatus() {
        User user =(User) SubjectUtil.getCurrentUser();
        return redisOpenService.hget(RedisKeyEnum.USER_ORDER_RECEIVE_TIME_KEY.generateKey(user.getId()));
    }


}
