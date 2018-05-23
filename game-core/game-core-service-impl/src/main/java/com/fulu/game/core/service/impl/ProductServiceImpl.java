package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.ProductDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.*;
import com.fulu.game.core.search.component.ProductSearchComponent;
import com.fulu.game.core.search.domain.ProductShowCaseDoc;
import com.fulu.game.core.service.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.xiaoleilu.hutool.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;


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
    @Autowired
    private ProductSearchComponent productSearchComponent;


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
        if(salesMode==null){
            throw new ServiceErrorException("单位不能为空!");
        }
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

    /**
     * 激活或者取消激活商品
     * @param id
     * @param status
     * @return
     */
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
    //todo 开始接单的时候要去校验商品字段有没有更新
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
                log.info("开始接单设置:userId:{};product:{};beginOrderDate:{};orderHour:{};", product.getUserId(), product, System.currentTimeMillis(), hour);
                redisOpenService.hset(RedisKeyEnum.PRODUCT_ENABLE_KEY.generateKey(product.getId()), BeanUtil.beanToMap(productVO), expire);
            } catch (Exception e) {
                log.error("开始接单设置失败", e);
                throw new ServiceErrorException("开始接单操作失败!");
            }
        }
        //添加商品到首页
        batchCreateUserProduct(user.getId());
    }


    /**
     * 停止接单
     */
    @Override
    public void stopOrderReceiving() {
        User user =(User) SubjectUtil.getCurrentUser();
        List<Product> products = findEnabledProductByUser(user.getId());
        redisOpenService.delete(RedisKeyEnum.USER_ORDER_RECEIVE_TIME_KEY.generateKey(user.getId()));
        for (Product product : products) {
            try {
                log.info("停止接单设置:userId:{};product:{};endOrderDate:{};", product.getUserId(), product, System.currentTimeMillis());
                redisOpenService.delete(RedisKeyEnum.PRODUCT_ENABLE_KEY.generateKey(product.getId()));
            } catch (Exception e) {
                log.error("停止接单设置失败", e);
                throw new ServiceErrorException("开始接单操作失败!");
            }
        }
        //修改首页商品的状态
        batchCreateUserProduct(user.getId());
    }




    /**
     * 查询用户详情页
     * @param productId
     * @return
     */
    @Override
    public ProductDetailsVO findDetailsByProductId(Integer productId) {
        Product product = findById(productId);
        UserInfoVO userInfo = userInfoAuthService.findUserCardByUserId(product.getUserId(),true,true,true,false);
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



    @Override
    public SimpleProductVO findSimpleProductByProductId(Integer productId) {
        Product product = findById(productId);
        UserInfoVO userInfo = userInfoAuthService.findUserCardByUserId(product.getUserId(),false,false,false,false);
        SimpleProductVO simpleProductVO = new SimpleProductVO();
        BeanUtil.copyProperties(product,simpleProductVO);
        simpleProductVO.setUserInfo(userInfo);
        return simpleProductVO;
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
    public PageInfo findProductShowCase(Integer categoryId,
                                        Integer gender,
                                        Integer pageNum,
                                        Integer pageSize,
                                        String orderBy) {

        PageInfo page = null;
        try {
            Page  searchResult = productSearchComponent.searchShowCaseDoc(categoryId, gender, pageNum, pageSize,orderBy);
            page = new PageInfo(searchResult);
        } catch (Exception e) {
            log.error("ProductShowCase查询异常",e);
            PageHelper.startPage(pageNum,pageSize,"create_time desc");
            List<ProductShowCaseVO> showCaseVOS = productDao.findProductShowCase(categoryId,gender);
            for(ProductShowCaseVO showCaseVO : showCaseVOS){
                UserInfoVO userInfoVO = userInfoAuthService.findUserCardByUserId(showCaseVO.getUserId(),false,false,true,false);
                showCaseVO.setNickName(userInfoVO.getNickName());
                showCaseVO.setGender(userInfoVO.getGender());
                showCaseVO.setMainPhoto(userInfoVO.getMainPhotoUrl());
                showCaseVO.setCity(userInfoVO.getCity());
                showCaseVO.setPersonTags(userInfoVO.getTags());
                showCaseVO.setOnLine(isProductStartOrderReceivingStatus(showCaseVO.getId()));
            }
            page = new PageInfo(showCaseVOS);
        }
        return page;
    }


    public PageInfo searchContent(int pageNum, int pageSize, String nickName){
        PageInfo page = null;
        try {
            Page  searchResult = productSearchComponent.findByNickName(pageNum,pageSize,nickName);
            page = new PageInfo(searchResult);
        }catch (Exception e){
            page = new PageInfo();
        }
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
     * 为用户所有商品添加索引
     * @param userId
     */
    public void batchCreateUserProduct(Integer userId){
        List<Product> products = findByUserId(userId);
        batchCreateProductIndex(products);
        List<Integer> rightfulProductIds = new ArrayList<>();
        for (Product product : products) {
            rightfulProductIds.add(product.getId());
        }
        //删除掉之前用户的垃圾商品数据
        List<ProductShowCaseDoc> productShowCaseDocList = productSearchComponent.findByUser(userId);
        for (ProductShowCaseDoc pdoc : productShowCaseDocList) {
            if (!rightfulProductIds.contains(pdoc.getId())) {
                productSearchComponent.deleteIndex(pdoc.getId());
            }
        }
    }


    /**
     * 批量创建商品索引
     * @param products
     */
    private void batchCreateProductIndex(List<Product> products){
        List<Product> showIndexProducts = getShowIndexProduct(products);
        for(Product product : products){
            if(showIndexProducts.contains(product)){
                createProductIndex(product,true);
            }else{
                createProductIndex(product,false);
            }
        }
    }

    /**
     * 查询那些可以在首页显示的商品
     * @param products
     * @return
     */
    private List<Product> getShowIndexProduct(List<Product> products){
        Map<Integer,List<Product>> categoryProductMap = new HashMap<>();
        for(Product product :products){
            Integer categoryId = product.getCategoryId();
            if(categoryProductMap.containsKey(categoryId)){
                categoryProductMap.get(categoryId).add(product);
            }else{
                categoryProductMap.put(categoryId, Lists.newArrayList(product));
            }
        }
        List<Product> showIndexProducts = new ArrayList<>();
        categoryProductMap.forEach((k,v)->{
           List<Product> waitProducts = new ArrayList<>();
           v.forEach((p)->{
               if(p.getStatus()){
                   waitProducts.add(p);
               }
           });
           if(!waitProducts.isEmpty()){
               waitProducts.sort((Product p1, Product p2) -> p2.getSalesModeRank().compareTo(p1.getSalesModeRank()));
               showIndexProducts.add(waitProducts.get(0));
           }
        });
        return showIndexProducts;
    }


    /**
     * 创建商品索引
     * @param product
     * @param isIndexShow
     * @return
     */
    private ProductShowCaseDoc createProductIndex(Product product,Boolean isIndexShow){
        ProductShowCaseDoc productShowCaseDoc = new ProductShowCaseDoc();
        BeanUtil.copyProperties(product,productShowCaseDoc);
        UserInfoVO userInfoVO = userInfoAuthService.findUserCardByUserId(productShowCaseDoc.getUserId(),false,false,true,false);
        productShowCaseDoc.setNickName(userInfoVO.getNickName());
        productShowCaseDoc.setGender(userInfoVO.getGender());
        productShowCaseDoc.setMainPhoto(userInfoVO.getMainPhotoUrl());
        productShowCaseDoc.setCity(userInfoVO.getCity());
        productShowCaseDoc.setIsIndexShow(isIndexShow);
        productShowCaseDoc.setPersonTags(userInfoVO.getTags());
        productShowCaseDoc.setOnLine(isProductStartOrderReceivingStatus(productShowCaseDoc.getId()));
        Boolean result = productSearchComponent.saveProductIndex(productShowCaseDoc);
        if (!result) {
            log.error("插入索引失败:{}", productShowCaseDoc);
            //todo 如果插入索引结果失败如何做处理
        }
        return productShowCaseDoc;
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
