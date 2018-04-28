package com.fulu.game.play.controller;


import com.fulu.game.common.Result;
import com.fulu.game.common.enums.TechAttrTypeEnum;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.TechValue;
import com.fulu.game.core.entity.vo.ProductShowCaseVO;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.ProductService;
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
    @Autowired
    private ProductService productService;

    /**
     * 查询所有陪玩业务
      * @return
     */
    @PostMapping(value = "all")
    public Result list(){
        List<Category> categoryList = categoryService.findAllAccompanyPlayCategory();
        return Result.success().data(categoryList);
    }

    /**
     * 分页查询陪玩业务
      * @return
     */
    @PostMapping(value = "list")
    public Result page(Integer pageNum,Integer pageSize){
        PageInfo<Category> categoryList = categoryService.list(pageNum,pageSize);
        return Result.success().data(categoryList);
    }

    /**
     *
     * @param categoryId
     * @param gender
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @RequestMapping(value = "/product/list")
    public Result findPageByProductId(Integer categoryId,
                                      Integer gender,
                                      Integer pageNum,
                                      Integer pageSize,
                                      String orderBy){
        PageInfo<ProductShowCaseVO> pageInfo = productService.findProductShowCase(categoryId,gender,pageNum,pageSize,orderBy);
        return Result.success().data(pageInfo);
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
