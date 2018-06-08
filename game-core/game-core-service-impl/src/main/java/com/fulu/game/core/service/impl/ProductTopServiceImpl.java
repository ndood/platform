package com.fulu.game.core.service.impl;


import com.fulu.game.common.utils.CollectionUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.ProductTopDao;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.ProductTop;
import com.fulu.game.core.entity.vo.ProductTopVO;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.ProductService;
import com.fulu.game.core.service.ProductTopService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class ProductTopServiceImpl extends AbsCommonService<ProductTop, Integer> implements ProductTopService {

    @Autowired
    private ProductTopDao productTopDao;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;


    @Override
    public ICommonDao<ProductTop, Integer> getDao() {
        return productTopDao;
    }


    @Override
    public void productTopPutAway(Integer id, Boolean status) {
        ProductTop productTop = findById(id);
        productTop.setStatus(status);
        productTop.setUpdateTime(new Date());
        update(productTop);
        productService.updateUserProductIndex(productTop.getUserId(),Boolean.FALSE);
    }

    @Override
    public ProductTop findByUserAndCategory(Integer userId, Integer categoryId){
        ProductTopVO param = new ProductTopVO();
        param.setUserId(userId);
        param.setCategoryId(categoryId);
        List<ProductTop> productTopList =  productTopDao.findByParameter(param);
        if(productTopList.isEmpty()){
            return null;
        }
        return productTopList.get(0);
    }



    @Override
    public int findTopSortByUserCategory(Integer userId, Integer categoryId) {
        ProductTopVO param = new ProductTopVO();
        param.setUserId(userId);
        param.setCategoryId(categoryId);
        param.setStatus(Boolean.TRUE);
        List<ProductTop> productTopList =  productTopDao.findByParameter(param);
        if(productTopList.isEmpty()){
            return 0;
        }
        return productTopList.get(0).getSort();
    }

    @Override
    public PageInfo<ProductTop> productList(int pageNum, int pageSize, String nickname, String mobile, Integer categoryId) {
        PageHelper.startPage(pageNum, pageSize,"id desc");

        List<ProductTop>  list = productTopDao.list(nickname,mobile,categoryId);
        List<ProductTopVO> voList = CollectionUtil.copyNewCollections(list,ProductTopVO.class);
        for(ProductTopVO productTop : voList){
            Category category =categoryService.findById(productTop.getCategoryId());
            productTop.setCategoryName(category.getName());
        }

        PageInfo page = new PageInfo(list);
        page.setList(voList);
        return page;
    }


}
