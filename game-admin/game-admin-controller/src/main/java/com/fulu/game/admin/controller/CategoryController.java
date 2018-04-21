package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.CategoryParentEnum;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.TechValue;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.TechValueService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController extends BaseController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TechValueService techValueService;

    @PostMapping(value = "/list")
    public Result list(Integer pageNum, Integer pageSize) {
        PageInfo<Category> categoryPageInfo = categoryService.find(pageNum, pageSize);
        return Result.success();
    }

    /**
     * 保存内容
     *
     * @return
     */
    @PostMapping(value = "/save")
    public Result save(@RequestParam(required = false, defaultValue = "1") Integer sort,
                       String name,
                       @RequestParam(required = false, defaultValue = "false") Boolean status,
                       BigDecimal charges,
                       String icon,
                       Integer id) {
        Category category = new Category();
        category.setName(name);
        category.setSort(sort);
        category.setStatus(status);
        category.setCharges(charges);
        category.setIcon(icon);
        category.setId(id);
        if (category.getId() == null) {
            category.setPid(CategoryParentEnum.ACCOMPANY_PLAY.getType());
            category.setCreateTime(new Date());
            category.setUpdateTime(new Date());
            categoryService.create(category);
        } else {
            category.setUpdateTime(new Date());
            categoryService.update(category);
        }
        return Result.success().data(category).msg("内容创建成功!");
    }


    /**
     * 创建销售方式
     *
     * @return
     */
    @PostMapping(value = "/salesmode/create")
    public Result salesModeCreate(Integer categoryId,
                                  String modeName) {
        TechValue techValue = techValueService.createSalesMode(categoryId, modeName);
        return Result.success().msg("销售方式创建成功!").data(techValue);
    }


}
