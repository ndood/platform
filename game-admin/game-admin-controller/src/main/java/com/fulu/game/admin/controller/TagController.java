package com.fulu.game.admin.controller;


import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Tag;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/api/v1/tag")
@RestController
public class TagController extends BaseController {

    @Autowired
    private TagService tagService;

    /**
     * 创建内容标签
     * @param categoryId
     * @param tagName
     * @return
     */
    @PostMapping(value = "/category/create")
    public Result categoryTagCreate(Integer categoryId,
                         String tagName) {
        Tag tag = tagService.create(categoryId,tagName);
        return Result.success().data(tag).msg("添加游戏标签成功!");
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
