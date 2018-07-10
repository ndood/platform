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
     * @param pid
     * @param name
     * @return
     */
//    @PostMapping(value = "/category/create")
//    public Result createCategoryTag(Integer categoryId,
//                                    Integer pid,
//                                    String name) {
//        Tag tag = tagService.create(categoryId, pid, name);
//        return Result.success().data(tag).msg("添加游戏标签成功!");
//    }

    /**
     * 内容管理-创建/修改标签组
     * @param id
     * @param categoryId
     * @param name
     * @param sort
     * @param most
     * @return
     */
    @PostMapping(value = "/group-tag/save")
    public Result createGroupTag(Integer id,
                                 Integer categoryId,
                                 String name,
                                 Integer sort,
                                 Integer most) {
        if(most!=null&&most<0){
            return Result.error().msg("最大可选标签数不能小于0!");
        }
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(name);
        tag.setType(TagTypeEnum.GAME.getType());
        tag.setGender(GenderEnum.ASEXUALITY.getType());
        tag.setPid(Constant.DEF_PID);
        tag.setUpdateTime(new Date());
        tag.setMost(most);
        tag.setSort(sort);
        tag.setCategoryId(categoryId);
        if (tag.getId() == null) {
            tag.setCreateTime(new Date());
            tagService.create(tag);
            return Result.success().data(tag).msg("内容管理-标签组创建成功!");
        } else {
            tagService.update(tag);
            return Result.success().data(tag).msg("内容管理-标签组修改成功!");
        }
    }

    /**
     * 内容管理-删除标签组（和相关子标签）
     * @param tag (id: 标签组id值)
     * @return
     */
    @PostMapping(value = "/group-tag/del")
    public Result delGroupTag(Tag tag) {
        boolean flag = tagService.delGroupTag(tag);
        if(!flag) {
            return Result.error().msg("删除失败");
        }
        return Result.success().msg("删除成功");
    }

    /**
     * 内容管理-创建/修改子标签
     * @param id
     * @param name
     * @param sort
     * @param pid
     * @return
     */
    @PostMapping(value = "/son-tag/create")
    public Result createSonTag(Integer id,
                               String name,
                               Integer sort,
                               Integer pid) {
            Tag tag = new Tag();
            tag.setId(id);
            tag.setType(TagTypeEnum.GAME.getType());
            tag.setName(name);
            tag.setGender(GenderEnum.ASEXUALITY.getType());
            tag.setSort(sort);
            tag.setPid(pid);
            tag.setUpdateTime(new Date());
            if (tag.getId() == null) {
                tag.setCreateTime(new Date());
                tagService.create(tag);
                return Result.success().data(tag).msg("内容管理-子标签创建成功!");
            } else {
                tagService.update(tag);
                return Result.success().data(tag).msg("内容管理-子标签修改成功!");
            }
    }

    /**
     * 内容管理-删除子标签
     * @param tag
     * @return
     */
    @PostMapping(value = "/son-tag/del")
    public Result delSonTag(Tag tag) {
        int result = tagService.deleteById(tag.getId());
        if(result <= 0) {
            return Result.error().msg("删除失败，子标签不存在");
        }else {
            return Result.success().msg("删除成功");
        }
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
     * @param name
     * @return
     */
    @PostMapping(value = "/group/save")
    public Result saveGroupTag(Integer id,
                               String name,
                               Integer sort,
                               Integer most) {
        if(most!=null&&most<0){
            return Result.error().msg("最大可选标签数不能小于0!");
        }
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
