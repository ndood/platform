package com.fulu.game.core.service.impl;


import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.GenderEnum;
import com.fulu.game.common.enums.TagTypeEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.vo.TagVO;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.PersonTagService;
import com.fulu.game.core.service.TechTagService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.fulu.game.core.dao.TagDao;
import com.fulu.game.core.entity.Tag;
import com.fulu.game.core.service.TagService;


@Service
@Slf4j
public class TagServiceImpl extends AbsCommonService<Tag,Integer> implements TagService {

    @Autowired
	private TagDao tagDao;
    @Autowired
    private TechTagService techTagService;
    @Autowired
    private PersonTagService personTagService;

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

    public List<Tag> findGroupTagByCategoryId(int categoryId){
        TagVO param = new TagVO();
        param.setCategoryId(categoryId);
        return tagDao.findByParameter(param);
    }


    public List<Tag> findAllCategoryTags(int categoryId){
        TagVO tagVO = new TagVO();
        tagVO.setType(TagTypeEnum.GAME.getType());
        tagVO.setCategoryId(categoryId);
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
    @Deprecated
    public TagVO oldFindTagsByCategoryId(Integer categoryId) {
        List<Tag> tagList = findGroupTagByCategoryId(categoryId);
        if(tagList.isEmpty()){
            return null;
        }
        return findTagsByTagPid(tagList.get(0).getId());
    }

    @Override
    public Tag create(Integer categoryId, Integer pid, String tagName) {
        Category category = categoryService.findById(categoryId);

        TagVO vo = new TagVO();
        vo.setCategoryId(categoryId);
        List<Tag> tagList = tagDao.findByParameter(vo);
        //该游戏没有标签组
        Tag parentTag = new Tag();
        if(CollectionUtil.isEmpty(tagList)){
            parentTag.setName(tagName);
            parentTag.setGender(GenderEnum.ASEXUALITY.getType());
            parentTag.setPid(Constant.DEF_PID);
            parentTag.setType(TagTypeEnum.GAME.getType());
            parentTag.setCreateTime(new Date());
            parentTag.setUpdateTime(new Date());
            parentTag.setName(category.getName() + "标签组");
            parentTag.setCategoryId(categoryId);
            create(parentTag);
            category.setUpdateTime(new Date());
            categoryService.update(category);
        }
        Tag tag = new Tag();
        tag.setName(tagName);
        if(parentTag.getId() == null) {
            tag.setPid(pid);
        }else {
            tag.setPid(parentTag.getId());
        }
        tag.setType(TagTypeEnum.GAME.getType());
        tag.setCreateTime(new Date());
        tag.setUpdateTime(new Date());
        tag.setGender(GenderEnum.ASEXUALITY.getType());
        create(tag);
        return tag;
    }

    @Override
    public Tag update(Integer id, String tagName,Integer gender) {
        Tag tag = findById(id);
        tag.setGender(gender);
        tag.setName(tagName);
        update(tag);
        techTagService.updateTechTagByTag(tag);
        personTagService.updatePersonTagByTag(tag);
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
    public List<Tag> findByPid(int tagPid) {
        TagVO tagVO = new TagVO();
        tagVO.setPid(tagPid);
        return tagDao.findByParameter(tagVO);
    }

    @Override
    public Boolean delGroupTag(Tag tag) {
        try {
            tagDao.deleteById(tag.getId());
            tagDao.deleteByPid(tag.getId());
        }catch (Exception e) {
            log.error("内容管理-删除标签组（和相关子标签）出错");
            return false;
        }
        return true;
    }

    @Override
    public List<Tag> findByTagIds(List<Integer> tagIds) {
        return tagDao.findByTagIds(tagIds);
    }
}
