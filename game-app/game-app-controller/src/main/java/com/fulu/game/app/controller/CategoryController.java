package com.fulu.game.app.controller;


import com.fulu.game.common.Result;
import com.fulu.game.common.enums.CategoryParentEnum;
import com.fulu.game.common.enums.TechAttrTypeEnum;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.ProductShowCaseVO;
import com.fulu.game.core.entity.vo.TagVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/category")
public class CategoryController extends BaseController {

    @Autowired
    private TechAttrService techAttrService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private SalesModeService salesModeService;
    @Autowired
    private TagService tagService;
    @Autowired
    private TechValueService techValueService;
    @Autowired
    private PriceRuleService priceRuleService;


    /**
     * 查询所有陪玩业务
     * @return
     */
    @PostMapping(value = "all")
    public Result list() {
        List<Category> categoryList = categoryService.findAllAccompanyPlayCategory();
        for (Category category : categoryList) {
            if (category.getIndexIcon() != null) {
                category.setIcon(category.getIndexIcon());
            }
        }
        return Result.success().data(categoryList);
    }

    /**
     * 获取一级分类列表（例如：游戏和娱乐）
     * @return
     */
    @PostMapping( value = "first-list")
    public Result firstList() {
        List<Category> categoryList = categoryService.findByPid(CategoryParentEnum.ACCOMPANY_PLAY.getType(), true);
        for (Category category : categoryList) {
            if (category.getIndexIcon() != null) {
                category.setIcon(category.getIndexIcon());
            }
        }
        return Result.success().data(categoryList);
    }

    /**
     * 通过pid获取子分类列表
     * @param pid
     * @return
     */
    @PostMapping( value = "find-by-pid")
    public Result findByPid(@RequestParam(required = true) Integer pid) {
        List<Category> categoryList = categoryService.findByPid(pid, true);
        for (Category category : categoryList) {
            if (category.getIndexIcon() != null) {
                category.setIcon(category.getIndexIcon());
            }
        }
        return Result.success().data(categoryList);
    }

    /**
     * 分页查询陪玩业务
     *
     * @return
     */
    @PostMapping(value = "list")
    public Result page(@RequestParam(required = true) Integer pageNum,
                       @RequestParam(required = true) Integer pageSize) {
        PageInfo<Category> categoryList = categoryService.list(pageNum, pageSize);
        return Result.success().data(categoryList);
    }

    /**
     * 分页查询所有商品
     *
     * @param categoryId
     * @param gender
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @param dans 单位信息
     * @param prices 价格信息
     * @return
     */
    @RequestMapping(value = "/product/list")
    public Result findPageByProductId(@RequestParam(required = true) Integer categoryId,
                                      Integer gender,
                                      @RequestParam(required = true) Integer pageNum,
                                      @RequestParam(required = true) Integer pageSize,
                                      String orderBy,
                                      String dans,
                                      String prices) {
        PageInfo<ProductShowCaseVO> pageInfo = productService.findProductShowCase(categoryId, gender, pageNum, pageSize, orderBy, dans, prices);
        return Result.success().data(pageInfo);
    }

    /**
     * 根据业务查询游戏销售方式
     *
     * @param categoryId
     * @return
     */
    @PostMapping(value = "/salesmode/list")
    public Result saleModel(@RequestParam(required = true) Integer categoryId) {
        List<SalesMode> salesModeList = salesModeService.findByCategory(categoryId);
        return Result.success().data(salesModeList);
    }

    /**
     * 查询游戏所有标签
     *
     * @param categoryId
     * @return
     */
    @PostMapping(value = "/tag/list")
    public Result techTags(@RequestParam(required = true) Integer categoryId) {
        TagVO tagVO = tagService.findTagsByCategoryId(categoryId);
        if (tagVO == null) {
            return Result.error().msg("该游戏没有设置标签!");
        }
        return Result.success().data(tagVO);
    }

    /**
     * 查询游戏所有段位
     *
     * @return
     */
    @PostMapping(value = "/dan/list")
    public Result danList(@RequestParam(required = true) Integer categoryId) {
        TechAttr techAttr = techAttrService.findByCategoryAndType(categoryId, TechAttrTypeEnum.DAN.getType());
        if (techAttr == null) {
            return Result.error().msg("该游戏没有设置段位信息!");
        }
        List<TechValue> techValueList = techValueService.findByTechAttrId(techAttr.getId());
        return Result.success().data(techValueList);
    }

    /**
     * 查询游戏所有定价规则
     *
     * @return
     */
    @PostMapping(value = "/price-rule/list")
    public Result priceRuleList(@RequestParam(required = true) Integer categoryId) {
        List<PriceRule> priceRuleList = priceRuleService.findByCategoryId(categoryId);
        return Result.success().data(priceRuleList);
    }


}
