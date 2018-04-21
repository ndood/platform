package com.fulu.game.admin.controller;


import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.GenderEnum;
import com.fulu.game.common.enums.TagTypeEnum;
import com.fulu.game.core.entity.Tag;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.TagService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


@RequestMapping("/api/v1/tag")
@RestController
public class TagController extends BaseController {

    @Autowired
    private TagService tagService;


    @PostMapping(value = "/list")
    public Result list(Integer pageNum,
                       Integer pageSize){
       PageInfo<Tag> page = tagService.parentList(pageNum,pageSize);
       return Result.success().data(page);
    }

    /**
     * 创建内容标签
     * @param categoryId
     * @param tagName
     * @return
     */
    @PostMapping(value = "/category/create")
    public Result createCategoryTag(Integer categoryId,
                                    String tagName) {
        Tag tag = tagService.create(categoryId,tagName);
        return Result.success().data(tag).msg("添加游戏标签成功!");
    }

    /**
     * 创建标签组
     * @param groupTagName
     * @return
     */
    @PostMapping(value = "/group/create")
    public Result createGroupTag(String groupTagName,
                                 Integer sort,
                                 Integer most){
        Tag tag = new Tag();
        tag.setName(groupTagName);
        tag.setType(TagTypeEnum.PERSON.getType());
        tag.setGender(GenderEnum.ASEXUALITY.getType());
        tag.setPid(Constant.DEF_PID);
        tag.setCreateTime(new Date());
        tag.setUpdateTime(new Date());
        tag.setMost(most);
        tag.setSort(sort);
        tagService.create(tag);
        return Result.success().data(tag).msg("标签组创建成功!");
    }

    @PostMapping(value = "/person/create")
    public Result createPersonTag(String tagName,
                                  Integer gender,
                                  Integer sort,
                                  Integer pid){
        Tag tag = new Tag();
        tag.setType(TagTypeEnum.PERSON.getType());
        tag.setName(tagName);
        tag.setGender(gender);
        tag.setSort(sort);
        tag.setPid(pid);
        tag.setCreateTime(new Date());
        tag.setUpdateTime(new Date());
        tagService.create(tag);
        return Result.success().data(tag).msg("标签创建成功!");
    }


    /**
     * 删除内容标签
     * @param id
     * @return
     */
    @PostMapping(value = "/del")
    public Result tagDelete(Integer id) {
        tagService.deleteById(id);
        return Result.success().msg("删除标签成功!");
    }



}
