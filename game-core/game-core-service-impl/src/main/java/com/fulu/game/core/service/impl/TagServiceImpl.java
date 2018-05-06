package com.fulu.game.core.service.impl;


import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.GenderEnum;
import com.fulu.game.common.enums.TagTypeEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.vo.TagVO;
import com.fulu.game.core.service.CategoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.util.BeanUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fulu.game.core.dao.TagDao;
import com.fulu.game.core.entity.Tag;
import com.fulu.game.core.service.TagService;



@Service
public class TagServiceImpl extends AbsCommonService<Tag,Integer> implements TagService {

    @Autowired
	private TagDao tagDao;

    @Autowired
    private CategoryService categoryService;

    @Override
    public ICommonDao<Tag, Integer> getDao() {
        return tagDao;
    }


    public List<Tag> findAllPersonTags(){
        TagVO tagVO = new TagVO();
        tagVO.setType(TagTypeEnum.PERSON.getType());
        return tagDao.findByParameter(tagVO);
    }


    @Override
    public TagVO findTagsByTagPid(Integer tagPid) {
        Tag tag = findById(tagPid);
        TagVO tagVO = new TagVO();
        BeanUtil.copyProperties(tag,tagVO);
        List<Tag> sonTagList = findByPid(tagPid);
        List<TagVO> sonTagVoList = new ArrayList<>();
        for(Tag sonTag : sonTagList){
            TagVO sonTagVo = new TagVO();
            BeanUtil.copyProperties(sonTag,sonTagVo);
            sonTagVoList.add(sonTagVo);
        }
        tagVO.setSonTags(sonTagVoList);
        return tagVO;
    }

    @Override
    public Tag create(Integer categoryId, String tagName) {
        Category category = categoryService.findById(categoryId);
        //该游戏没有没有标签组
        if(category.getTagId()==null){
            Tag parentTag = new Tag();
            parentTag.setName(tagName);
            parentTag.setGender(GenderEnum.ASEXUALITY.getType());
            parentTag.setPid(Constant.DEF_PID);
            parentTag.setType(TagTypeEnum.GAME.getType());
            parentTag.setCreateTime(new Date());
            parentTag.setUpdateTime(new Date());
            parentTag.setName(category.getName()+"标签组");
            create(parentTag);
            category.setTagId(parentTag.getId());
            category.setUpdateTime(new Date());
            categoryService.update(category);
        }
        Tag tag = new Tag();
        tag.setName(tagName);
        tag.setPid(category.getTagId());
        tag.setType(TagTypeEnum.GAME.getType());
        tag.setCreateTime(new Date());
        tag.setUpdateTime(new Date());
        tag.setGender(GenderEnum.ASEXUALITY.getType());
        create(tag);
        return tag;
    }

    @Override
    public PageInfo<Tag> parentList(Integer pageNum, Integer pageSize) {
        TagVO tagVO = new TagVO();
        tagVO.setPid(Constant.DEF_PID);
        tagVO.setType(TagTypeEnum.PERSON.getType());
        PageHelper.startPage(pageNum, pageSize);
        List<Tag> tagList = tagDao.findByParameter(tagVO);
        PageInfo page = new PageInfo(tagList);
        return page;
    }



    @Override
    public List<Tag> findByPid(Integer tagPid) {
        TagVO tagVO = new TagVO();
        tagVO.setPid(tagPid);
        return tagDao.findByParameter(tagVO);
    }


}
