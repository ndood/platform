package com.fulu.game.play.controller;


import com.fulu.game.common.Result;
import com.fulu.game.common.enums.TechAttrTypeEnum;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.TechValue;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.TechAttrService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/category")
public class CategoryController extends BaseController{

    @Autowired
    private TechAttrService techAttrService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 查询所有陪玩业务
      * @return
     */
    @PostMapping(value = "list")
    public Result list(){
        List<Category> categoryList = categoryService.findAllAccompanyPlayCategory();
        return Result.success().data(categoryList);
    }

    /**
     * 查询所有陪玩业务
      * @return
     */
    @PostMapping(value = "page")
    public Result page(Integer pageNum,Integer pageSize){
        PageInfo<Category> categoryList = categoryService.list(pageNum,pageSize);
        return Result.success().data(categoryList);
    }


    /**
     * 根据业务查询游戏销售方式
     * @param categoryId
     * @return
     */
    @PostMapping(value = "salesmode/list")
    public Result saleModel(Integer categoryId){
        List<TechValue> techValueList = techAttrService.findValByCategoryAndType(categoryId, TechAttrTypeEnum.SALES_MODE.getType());
        return Result.success().data(techValueList);
    }





}
