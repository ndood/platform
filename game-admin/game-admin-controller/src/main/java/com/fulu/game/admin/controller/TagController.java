package com.fulu.game.admin.controller;


import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.GenderEnum;
import com.fulu.game.common.enums.TagTypeEnum;
import com.fulu.game.core.entity.Tag;
import com.fulu.game.core.entity.vo.TagVO;
import com.fulu.game.core.service.TagService;
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
                       Integer pageSize) {
        PageInfo<Tag> page = tagService.parentList(pageNum, pageSize);
        return Result.success().data(page);
    }

    /**
     * 查询标签组
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/group/query")
    public Result groupInfo(Integer id) {
        TagVO tagVO = tagService.findTagsByTagPid(id);
        return Result.success().data(tagVO);
    }

    /**
     * 创建内容标签
     * @param categoryId
     * @param name
     * @return
     */
    @PostMapping(value = "/category/create")
    public Result createCategoryTag(Integer categoryId,
                                    String name) {
        Tag tag = tagService.create(categoryId, name);
        return Result.success().data(tag).msg("添加游戏标签成功!");
    }

    @PostMapping(value = "/update")
    public Result updateCategoryTag(Integer id,
                                    Integer gender,
                                    String name) {
        Tag tag = tagService.update(id, name,gender);
        return Result.success().data(tag).msg("修改标签成功!");
    }

    /**
     * 创建标签组
     *
     * @param name
     * @return
     */
    @PostMapping(value = "/group/save")
    public Result saveGroupTag(Integer id,
                               String name,
                               Integer sort,
                               Integer most) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(name);
        tag.setType(TagTypeEnum.PERSON.getType());
        tag.setGender(GenderEnum.ASEXUALITY.getType());
        tag.setPid(Constant.DEF_PID);
        tag.setUpdateTime(new Date());
        tag.setMost(most);
        tag.setSort(sort);
        if (tag.getId() == null) {
            tag.setCreateTime(new Date());
            tagService.create(tag);
            return Result.success().data(tag).msg("标签组创建成功!");

        } else {
            tagService.update(tag);
            return Result.success().data(tag).msg("标签修改成功!");
        }
    }

    /**
     * 标签保持
     *
     * @param id
     * @param name
     * @param gender
     * @param sort
     * @param pid
     * @return
     */
    @PostMapping(value = "/person/save")
    public Result savePersonTag(Integer id,
                                String name,
                                Integer gender,
                                Integer sort,
                                Integer pid) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setType(TagTypeEnum.PERSON.getType());
        tag.setName(name);
        tag.setGender(gender);
        tag.setSort(sort);
        tag.setPid(pid);
        tag.setUpdateTime(new Date());
        if (tag.getId() == null) {
            tag.setCreateTime(new Date());
            tagService.create(tag);
            return Result.success().data(tag).msg("标签创建成功!");
        } else {
            tagService.update(tag);
            return Result.success().data(tag).msg("标签修改成功!");
        }
    }


    /**
     * 删除内容标签
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/del")
    public Result tagDelete(Integer id) {
        tagService.deleteById(id);
        return Result.success().msg("删除标签成功!");
    }


}
