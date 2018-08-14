package com.fulu.game.h5.controller.fenqile;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.TechAttrTypeEnum;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.SalesMode;
import com.fulu.game.core.entity.TechAttr;
import com.fulu.game.core.entity.TechValue;
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
 * 分类Controller
 *
 * @author Gong ZeChun
 * @date 2018/8/13 15:53
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/category")
public class CategoryController extends BaseController {
    private final TechAttrService techAttrService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final SalesModeService salesModeService;
    private final TagService tagService;
    private final TechValueService techValueService;

    @Autowired
    public CategoryController(TechAttrService techAttrService,
                              CategoryService categoryService,
                              ProductService productService,
                              SalesModeService salesModeService,
                              TagService tagService,
                              TechValueService techValueService) {
        this.techAttrService = techAttrService;
        this.categoryService = categoryService;
        this.productService = productService;
        this.salesModeService = salesModeService;
        this.tagService = tagService;
        this.techValueService = techValueService;
    }

    /**
     * 查询所有分期乐业务
     *
     * @return 封装结果集
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
     * 分页查询分期乐业务
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数据条数
     * @return 封装结果集
     */
    @PostMapping(value = "list")
    public Result page(@RequestParam Integer pageNum,
                       @RequestParam Integer pageSize) {
        PageInfo<Category> categoryList = categoryService.list(pageNum, pageSize);
        return Result.success().data(categoryList);
    }

    /**
     * 分页查询所有商品
     *
     * @param categoryId 分类id
     * @param gender     性别
     * @param pageNum    页码
     * @param pageSize   每页显示数据条数
     * @param orderBy    排序字符串
     * @return 封装结果集
     */
    @RequestMapping(value = "/product/list")
    public Result findPageByProductId(@RequestParam Integer categoryId,
                                      Integer gender,
                                      @RequestParam Integer pageNum,
                                      @RequestParam Integer pageSize,
                                      String orderBy) {
        PageInfo<ProductShowCaseVO> pageInfo = productService.findProductShowCase(categoryId, gender, pageNum,
                pageSize, orderBy);
        return Result.success().data(pageInfo);
    }

    /**
     * 根据业务查询游戏销售方式
     *
     * @param categoryId 分类id
     * @return 封装结果集
     */
    @PostMapping(value = "/salesmode/list")
    public Result saleModel(@RequestParam Integer categoryId) {
        List<SalesMode> salesModeList = salesModeService.findByCategory(categoryId);
        return Result.success().data(salesModeList);
    }


    /**
     * 查询游戏所有标签
     *
     * @param categoryId 分类id
     * @return 封装结果集
     */
    @PostMapping(value = "/tag/list")
    public Result techTags(@RequestParam Integer categoryId) {
        TagVO tagVO = tagService.oldFindTagsByCategoryId(categoryId);
        if (tagVO == null) {
            return Result.error().msg("该游戏没有设置标签!");
        }
        return Result.success().data(tagVO);
    }

    /**
     * 查询游戏所有段位
     *
     * @param categoryId 分类id
     * @return 封装结果集
     */
    @PostMapping(value = "/dan/list")
    public Result danList(@RequestParam Integer categoryId) {
        TechAttr techAttr = techAttrService.findByCategoryAndType(categoryId, TechAttrTypeEnum.DAN.getType());
        if (techAttr == null) {
            return Result.error().msg("该游戏没有设置段位信息!");
        }
        List<TechValue> techValueList = techValueService.findByTechAttrId(techAttr.getId());
        return Result.success().data(techValueList);
    }
}
