package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.CategoryParentEnum;
import com.fulu.game.common.enums.TechAttrTypeEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.CategoryVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fulu.game.core.dao.CategoryDao;


@Service
public class CategoryServiceImpl extends AbsCommonService<Category,Integer> implements CategoryService {

    @Autowired
	private CategoryDao categoryDao;
    @Autowired
    private TechAttrService techAttrService;
    @Autowired
    private TechValueService techValueService;
    @Autowired
    private TagService tagService;
    @Autowired
    private SalesModeService salesModeService;

    @Override
    public ICommonDao<Category, Integer> getDao() {
        return categoryDao;
    }

    @Override
    public PageInfo<Category> list(int pageNum, int pageSize) {
        return list(pageNum,pageSize,true,null);
    }

    @Override
    public PageInfo<Category> list(int pageNum, int pageSize, Boolean status,String orderBy) {
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setStatus(status);
        categoryVO.setPid(CategoryParentEnum.ACCOMPANY_PLAY.getType());
        if(StringUtils.isBlank(orderBy)){
            orderBy = "sort desc";
        }
        PageHelper.startPage(pageNum,pageSize,orderBy);
        List<Category> categoryList = categoryDao.findByParameter(categoryVO);
        PageInfo page = new PageInfo(categoryList);
        return page;
    }


    @Override
    public CategoryVO findCategoryVoById(Integer id) {
        Category category = categoryDao.findById(id);
        category.setCharges(category.getCharges().multiply(new BigDecimal(100)));
        CategoryVO categoryVO = new CategoryVO();
        BeanUtil.copyProperties(category,categoryVO);
        //查询游戏销售方式
        List<SalesMode> salesModes = salesModeService.findByCategory(category.getId());
        categoryVO.setSalesModeList(salesModes);
        //查询游戏段位
        List<TechAttr> techAttrList = techAttrService.findByCategory(category.getId());
        for (TechAttr techAttr : techAttrList) {
            if (TechAttrTypeEnum.DAN.getType().equals(techAttr.getType())) {
                List<TechValue> danList = techValueService.findByTechAttrId(techAttr.getId());
                categoryVO.setDanList(danList);
            }
        }
        //查询游戏标签
        if(category.getTagId()!=null){
            Tag parentTag = tagService.findById(category.getTagId());
            categoryVO.setMost(parentTag.getMost());
            List<Tag> tagList = tagService.findByPid(category.getTagId());
            categoryVO.setTagList(tagList);
        }
        return categoryVO;
    }


    @Override
    public List<Category> findAllAccompanyPlayCategory(){
        return findByPid(CategoryParentEnum.ACCOMPANY_PLAY.getType(),true);
    }


    @Override
    public List<Category> findByPid(Integer pid,Boolean status) {
        PageHelper.orderBy("sort desc");
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setPid(pid);
        categoryVO.setStatus(status);
        List<Category> categoryList =categoryDao.findByParameter(categoryVO);
        return categoryList;
    }


}
