package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.CategoryParentEnum;
import com.fulu.game.common.enums.TechAttrTypeEnum;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.core.dao.CategoryDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.TagDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.CategoryVO;
import com.fulu.game.core.entity.vo.TagVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.util.BeanUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Service
public class CategoryServiceImpl extends AbsCommonService<Category, Integer> implements CategoryService {

    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private TechAttrService techAttrService;
    @Autowired
    private TechValueService techValueService;
    @Autowired
    private TagService tagService;
    @Autowired
    private SalesModeService salesModeService;
    @Autowired
    private OssUtil ossUtil;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserTechAuthService userTechAuthService;

    @Override
    public ICommonDao<Category, Integer> getDao() {
        return categoryDao;
    }

    @Override
    public PageInfo<Category> list(int pageNum, int pageSize) {
        return list(pageNum, pageSize, true, null);
    }

    @Override
    public PageInfo<Category> list(int pageNum, int pageSize, Boolean status, String orderBy) {
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setStatus(status);
        categoryVO.setPid(CategoryParentEnum.ACCOMPANY_PLAY.getType());
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "sort desc";
        }
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<Category> categoryList = categoryDao.findByParameter(categoryVO);
        PageInfo page = new PageInfo(categoryList);
        return page;
    }


    @Override
    public CategoryVO findCategoryVoById(Integer id) {
        Category category = categoryDao.findById(id);
        category.setCharges(category.getCharges().multiply(new BigDecimal(100)));
        CategoryVO categoryVO = new CategoryVO();
        BeanUtil.copyProperties(category, categoryVO);
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
        //查询游戏大区
        for (TechAttr techAttr : techAttrList) {
            if (TechAttrTypeEnum.AREA.getType().equals(techAttr.getType())) {
                List<TechValue> areaList = techValueService.findByTechAttrId(techAttr.getId());
                categoryVO.setAreaList(areaList);
            }
        }
        //查询游戏标签
//        if (category.getTagId() != null) {
//            Tag parentTag = tagService.findById(category.getTagId());
//            categoryVO.setMost(parentTag.getMost());
//            List<Tag> tagList = tagService.findByPid(category.getTagId());
//            categoryVO.setTagList(tagList);
//        }

        //查询游戏标签
        TagVO tagVO = new TagVO();
        tagVO.setCategoryId(id);
        List<Tag> pTagList = tagDao.findByParameter(tagVO);
        List<TagVO> tagVOList = new ArrayList<>();

        if(CollectionUtils.isEmpty(pTagList)) {
            return categoryVO;
        }
        for(Tag pTag : pTagList) {
            TagVO vo = new TagVO();
            BeanUtil.copyProperties(pTag, vo);

            //查询子标签
            List<Tag> sonTags = tagService.findByPid(pTag.getId());
            List<TagVO> sonTagVOList = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(sonTags)) {
                for(Tag meta : sonTags) {
                    TagVO vo1 = new TagVO();
                    BeanUtil.copyProperties(meta, vo1);
                    sonTagVOList.add(vo1);
                }
            }
            vo.setSonTags(sonTagVOList);
            tagVOList.add(vo);
        }
        categoryVO.setGroupTags(tagVOList);
        return categoryVO;
    }


    @Override
    public List<Category> findAllAccompanyPlayCategory() {
        return findByPid(CategoryParentEnum.ACCOMPANY_PLAY.getType(), true);
    }


    @Override
    public Category save(CategoryVO categoryVO) {
        Category category = new Category();
        BeanUtil.copyProperties(categoryVO,category);
        if(category.getId()==null){
            category.setPid(CategoryParentEnum.ACCOMPANY_PLAY.getType());
            category.setCreateTime(new Date());
            category.setUpdateTime(new Date());
            create(category);
        }else{
            Category origCategory = findById(categoryVO.getId());
            if(categoryVO.getMost()!=null&&origCategory.getTagId()!=null){
                Tag tag = new Tag();
                tag.setId(origCategory.getTagId());
                tag.setMost(categoryVO.getMost());
                tagService.update(tag);
            }
            if(!Objects.equals(origCategory.getIcon(),category.getIcon())){
                ossUtil.deleteFile(origCategory.getIcon());
            }
            if(!Objects.equals(origCategory.getIndexIcon(),category.getIndexIcon())){
                ossUtil.deleteFile(origCategory.getIndexIcon());
            }
            category.setUpdateTime(new Date());
            update(category);
            //同步更新商品表的冗余数据
            productService.updateByCategory(category);
            userTechAuthService.updateByCategory(category);
        }
        return category;
    }


    @Override
    public List<Category> findByPid(Integer pid, Boolean status) {
        PageHelper.orderBy("sort desc");
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setPid(pid);
        categoryVO.setStatus(status);
        List<Category> categoryList = categoryDao.findByParameter(categoryVO);
        return categoryList;
    }


}
