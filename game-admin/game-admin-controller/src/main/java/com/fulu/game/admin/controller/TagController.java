package com.fulu.game.admin.controller;


import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/tag")
public class TagController extends CommonController {

    @Autowired
    private TagService tagService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping(value = "create")
    public Result create(Integer categoryId,
                         String tagName) {
        Category category = categoryService.findById(categoryId);


        return Result.success();
    }

}
