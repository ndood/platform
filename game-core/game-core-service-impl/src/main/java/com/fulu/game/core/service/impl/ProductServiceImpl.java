package com.fulu.game.core.service.impl;


import com.fulu.game.common.Constant;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.ProductVO;
import com.fulu.game.core.service.*;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.BeanUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.ProductDao;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
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
        if(redisOpenService.hasKey(generateKey(id))){
            throw new ServiceErrorException("在线技能不允许修改!");
        }
        Product product = findById(id);
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
        if(redisOpenService.hasKey(generateKey(id))){
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
    public List<Product> findEnabledProductByUser(int userId){
        ProductVO productVO = new ProductVO();
        productVO.setStatus(true);
        return productDao.findByParameter(productVO);
    }

    /**
     * 开始接单业务
     */
    public void startOrderReceiving(int hour){
        List<Product>  products =  findEnabledProductByUser(Constant.DEF_USER_ID);
        if(products.isEmpty()){
            throw new ServiceErrorException("请选择技能后再点击开始接单!");
        }
        redisOpenService.hset(UserServiceImpl.generateKey(Constant.DEF_USER_ID),"HOUR",hour+"");
        redisOpenService.hset(UserServiceImpl.generateKey(Constant.DEF_USER_ID),"START_TIME", DateUtil.now());
        for(Product product : products){
            ProductVO productVO = new ProductVO();
            BeanUtil.copyProperties(product,productVO);
            productVO.setHour(hour);
            productVO.setStartTime(new Date());
            try {
                log.info("开始接单设置{}小时【{}】",hour,productVO);
                redisOpenService.hset(generateKey(product.getId()),productVO,hour*3600L);
            } catch (Exception e) {
                log.error("开始接单设置",e);
                throw  new ServiceErrorException("开始接单操作失败!");
            }
        }
    }

    @Override
    public void stopOrderReceiving() {
        List<Product>  products =  findEnabledProductByUser(Constant.DEF_USER_ID);
        redisOpenService.delete(UserServiceImpl.generateKey(Constant.DEF_USER_ID));
        for(Product product : products){
            try {
                log.info("停止接单{}",product);
                redisOpenService.delete(generateKey(product.getId()));
            } catch (Exception e) {
                log.error("开始接单设置",e);
                throw  new ServiceErrorException("开始接单操作失败!");
            }
        }
    }

    public Map<String,Object> readOrderReceivingStatus(){
       return redisOpenService.hget(UserServiceImpl.generateKey(Constant.DEF_USER_ID));
    }

    /**
     *
      * @param productId
     * @return
     */
    public static String generateKey(int productId){
        return Constant.REDIS_PRODUCT_ENABLE_KEY+"-"+productId;
    }

}
