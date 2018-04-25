package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.CategoryParentEnum;
import com.fulu.game.common.enums.TechAttrTypeEnum;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.Tag;
import com.fulu.game.core.entity.TechAttr;
import com.fulu.game.core.entity.TechValue;
import com.fulu.game.core.entity.vo.CategoryVO;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.TagService;
import com.fulu.game.core.service.TechAttrService;
import com.fulu.game.core.service.TechValueService;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController extends BaseController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TechValueService techValueService;
    @Autowired
    private TechAttrService techAttrService;
    @Autowired
    private TagService tagService;

    /**
     * 内容列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/list")
    public Result list(Integer pageNum,
                       Integer pageSize,
                       Boolean status) {
        PageInfo<Category> page = categoryService.list(pageNum, pageSize,status,null);
        return Result.success().data(page);
    }

    /**
     * 查询所有内容
     * @return
     */
    @PostMapping(value = "/list-all")
    public Result listAll(){
        List<Category> list = categoryService.findByPid(CategoryParentEnum.ACCOMPANY_PLAY.getType());
        return Result.success().data(list);
    }

    /**
     * 查询单个内容所有信息
     * @param categoryId
     * @return
     */
    @PostMapping(value = "/query")
    public Result info(Integer categoryId){
        CategoryVO categoryVO =categoryService.findCategoryVoById(categoryId);
        return Result.success().data(categoryVO);
    }

    /**
     * 保存内容
     *
     * @return
     */
    @PostMapping(value = "/save")
    public Result save(Integer sort,
                       String name,
                       Boolean status,
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
            return Result.success().data(category).msg("内容创建成功!");
        } else {
            category.setUpdateTime(new Date());
            categoryService.update(category);
        }
        return Result.success().data(category).msg("内容修改成功!");
    }


    /**
     * 创建销售方式
     *
     * @return
     */
    @PostMapping(value = "/salesmode/create")
    public Result salesModeCreate(Integer categoryId,
                                  String name,
                                  Integer rank) {
        TechValue techValue = techValueService.createSalesMode(categoryId, name,rank);
        return Result.success().msg("销售方式创建成功!").data(techValue);
    }

    /**
     * 删除销售方式
     * @return
     */
    @PostMapping(value = "/salesmode/delete")
    public Result saleModeDelete(Integer id){
        techValueService.deleteById(id);
        return Result.success().msg("销售方式删除成功!");
    }

    /**
     * 创建段位
     **/
    @PostMapping(value = "/dan/create")
    public Result danCreate(Integer categoryId,
                            String name,
                            Integer rank) {
        TechValue techValue = techValueService.createDan(categoryId, name,rank);
        return Result.success().msg("段位创建成功!").data(techValue);
    }

    /**
     * 删除段位
     * @param id
     * @return
     */
    @PostMapping(value = "/dan/delete")
    public Result danDelete(Integer id){
        techValueService.deleteById(id);
        return Result.success().msg("段位删除成功!");
    }


    /**
     * 查询游戏所有标签
     * @param categoryId
     * @return
     */
    @PostMapping(value = "/tag/list")
    public Result techTags(Integer categoryId){
        Category category =categoryService.findById(categoryId);
        if(category.getTagId()==null){
            return Result.error().msg("该游戏没有设置标签!");
        }
        List<Tag> tagList = tagService.findByPid(category.getTagId());
        return Result.success().data(tagList);
    }

    /**
     * 查询游戏所有段位
     * @return
     */
    @PostMapping(value = "/dan/list")
    public Result danList(@RequestParam(required=true,name = "categoryId") Integer categoryId){
        TechAttr techAttr = techAttrService.findByCategoryAndType(categoryId, TechAttrTypeEnum.DAN.getType());
        List<TechValue>  techValueList =   techValueService.findByTechAttrId(techAttr.getId());
        return Result.success().data(techValueList);
    }



}
