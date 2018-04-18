package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.service.CategoryService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/v1/category")
public class CategoryController extends CommonController{

    @Autowired
    private CategoryService categoryService;

    @PostMapping(value = "/list")
    public Result list(Integer pageNum,Integer pageSize){
        PageInfo<Category> categoryPageInfo =  categoryService.find(pageNum,pageSize);
        return Result.success();
    }



}
