package com.fulu.game.app.controller;

import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.PlatformShowEnum;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.AssignOrderSettingVO;
import com.fulu.game.core.entity.vo.PriceRuleVO;
import com.fulu.game.core.entity.vo.ProductDetailsVO;
import com.fulu.game.core.entity.vo.TechAuthProductVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/9/4 18:42.
 * @Description:
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/product")
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private TechTagService techTagService;
    @Autowired
    private UserTechAuthService userTechAuthService;
    @Autowired
    private PriceRuleService priceRuleService;
    @Autowired
    private AssignOrderSettingService assignOrderSettingService;
    @Autowired
    private TagService tagService;
    /**
     * 获取商品列表
     * @param userId
     * @return
     */
    @PostMapping(value = "/user/list")
    public Result productList(@RequestParam(required = false) Integer  userId) {
        if(userId == null || userId.intValue() < 0){
            User user = userService.getCurrentUser();
            userId = user.getId();
        }
        List<Product> list = productService.findAppProductList(userId);
        return Result.success().data(list);
    }

    /**
     * 查询用户商品详情页
     *
     * @param productId
     * @return
     */
    @RequestMapping(value = "/details")
    public Result findByProductId(Integer productId) {
        ProductDetailsVO productDetailsVO = productService.findDetailsByProductId(productId);
        return Result.success().data(productDetailsVO);
    }


    /**
     * 用户所有接单方式列表
     * @return
     */
    @RequestMapping(value = "/order-receive/tech/list")
    public Result techList() {
        User user = userService.getCurrentUser();
        Map<String,Object> map = new HashMap<>();
        AssignOrderSettingVO assignOrderSettingVO =    assignOrderSettingService.findByUserId(user.getId());
        List<TechAuthProductVO> techAuthProductVOS = productService.techAuthProductList(user.getId(), PlatformShowEnum.APP);
        map.put("assignOrderSetting",assignOrderSettingVO);
        map.put("techList",techAuthProductVOS);
        return Result.success().data(map);
    }


    /**
     * 技能接单方式激活
     * @param techId
     * @param status
     * @return
     */
    @RequestMapping(value = "/order-receive/tech/enable")
    public Result techEnable(@RequestParam(required = true) Integer techId,
                             @RequestParam(required = true) Boolean status) {
        User user = userService.getCurrentUser();
        productService.techEnable(techId, status,user.getId());
        if (status) {
            return Result.success().msg("开启");
        } else {
            return Result.success().msg("关闭");
        }
    }



    @RequestMapping(value = "/category/tags")
    public Result allCategoryUserTags(@RequestParam(required = true)Integer categoryId){
        User user = userService.getCurrentUser();
        List<Tag> tagList  =   tagService.findByUserCategoryId(user.getId(),categoryId);
        return Result.success().data(tagList);
    }



    /**
     * 设置技能为主要技能
     * @param techId
     * @return
     */
    @RequestMapping(value = "/order-receive/tech/main")
    public Result techMain(@RequestParam(required = true) Integer techId) {
        userTechAuthService.settingsTechMain(techId);
        return Result.success().msg("设置成功!");
    }



    /**
     * 保存陪玩师的技能标签
     * @param techId
     * @return
     */
    @RequestMapping(value = "/tech-tag/save")
    public Result saveTechTag(@RequestParam(required = true) Integer techId,
                              @RequestParam(required = true) Integer[] tagIds) {
        techTagService.saveTechTag(techId,tagIds);
        return Result.success().msg("开启");
    }



    /**
     * 保存用户接单方式价格
     * @param techId
     * @param price
     * @param unitId
     * @return
     */
    @RequestMapping(value = "/order-receive/save")
    public Result save(@RequestParam(required = true) Integer techId,
                       @RequestParam(required = true) BigDecimal price,
                       @RequestParam(required = true) Integer priceId,
                       @RequestParam(required = true) Integer unitId) {
        if (new BigDecimal(Constant.DEF_RECEIVING_ORDER_PRICE).compareTo(price) > 0) {
            return Result.error().msg("接单价格不能低于" + Constant.DEF_RECEIVING_ORDER_PRICE + "元");
        }
        PriceRule priceRule = priceRuleService.findById(priceId);
        if(priceRule==null){
            return Result.error().msg("修改价格失败!");
        }
        productService.save(techId, priceRule.getPrice(), unitId);
        return Result.success().msg("修改价格成功!");
    }



    @RequestMapping(value = "/order-receive/assign_order_sett/save")
    public Result assignOrder(AssignOrderSettingVO assignOrderSettingVO){
        User user = userService.getCurrentUser();
        assignOrderSettingVO.setUserId(user.getId());
        assignOrderSettingService.save(assignOrderSettingVO);
        return Result.success().data(assignOrderSettingVO).msg("设置成功！");
    }






}
