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
    @Autowired
    private CategoryService categoryService;

    @PostMapping(value = "/category/create")
    public Result create(Integer categoryId,
                         String tagName) {
        Tag tag = tagService.create(categoryId,tagName);
        return Result.success().data(tag);
    }






}
