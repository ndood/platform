package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.CategoryParentEnum;
import com.fulu.game.common.enums.TechAttrTypeEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Tag;
import com.fulu.game.core.entity.TechAttr;
import com.fulu.game.core.entity.TechValue;
import com.fulu.game.core.entity.vo.CategoryVO;
import com.fulu.game.core.service.TagService;
import com.fulu.game.core.service.TechAttrService;
import com.fulu.game.core.service.TechValueService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.fulu.game.core.dao.CategoryDao;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.service.CategoryService;



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

    @Override
    public ICommonDao<Category, Integer> getDao() {
        return categoryDao;
    }

    @Override
    public PageInfo<Category> list(int pageNum, int pageSize, Boolean status,String orderBy) {
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setStatus(status);
        categoryVO.setPid(CategoryParentEnum.ACCOMPANY_PLAY.getType());
        if(StringUtils.isNotBlank(orderBy)){
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
        CategoryVO categoryVO = new CategoryVO();
        BeanUtil.copyProperties(category,categoryVO);

        //查询销售方式和段位
        List<TechAttr> techAttrList = techAttrService.findByCategory(category.getId());
        for(TechAttr techAttr : techAttrList){
            if(TechAttrTypeEnum.SALES_MODE.getType().equals(techAttr.getType())){
                List<TechValue>  salesModeList =techValueService.findByTechAttrId(techAttr.getId());
                categoryVO.setSalesModeList(salesModeList);
            }
            if(TechAttrTypeEnum.DAN.getType().equals(techAttr.getType())){
                List<TechValue> danList = techValueService.findByTechAttrId(techAttr.getId());
                categoryVO.setDanList(danList);
            }
        }
        if(category.getPid()!=null){
            List<Tag> tagList = tagService.findByPid(category.getPid());
            categoryVO.setTagList(tagList);
        }
        return categoryVO;
    }
}
