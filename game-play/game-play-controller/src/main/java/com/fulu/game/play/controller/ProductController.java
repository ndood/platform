package com.fulu.game.play.controller;

import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.PlatformShowEnum;
import com.fulu.game.common.exception.ProductException;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.PriceRule;
import com.fulu.game.core.entity.Product;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.*;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.UserTechAuthServiceImpl;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/product")
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;
    @Autowired
    private MoneyDetailsService moneyDetailsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private TechTagService techTagService;
    @Autowired
    private UserTechAuthServiceImpl userTechAuthService;
    @Autowired
    private PriceRuleService priceRuleService;
    @Autowired
    private AssignOrderSettingService assignOrderSettingService;


    @RequestMapping(value = "/search")
    public Result search(@RequestParam(required = true) Integer pageNum,
                         @RequestParam(required = true) Integer pageSize,
                         @RequestParam(required = true) String content) {
        PageInfo pageInfo = productService.searchContent(pageNum, pageSize, content);
        return Result.success().data(pageInfo);
    }


    /**
     * 添加接单方式
     *
     * @param techAuthId
     * @param price
     * @param unitId
     * @return
     */
    @RequestMapping(value = "/order-receive/create")
    public Result create(@RequestParam(required = true) Integer techAuthId,
                         @RequestParam(required = true) BigDecimal price,
                         @RequestParam(required = true) Integer unitId) {
        if (new BigDecimal(Constant.DEF_RECEIVING_ORDER_PRICE).compareTo(price) > 0) {
            return Result.error().msg("接单价格不能低于" + Constant.DEF_RECEIVING_ORDER_PRICE + "元");
        }
        productService.create(techAuthId, price, unitId);
        return Result.success().msg("添加接单方式成功!");
    }

    /**
     * 修改接单方式
     *
     * @param techAuthId
     * @param price
     * @param unitId
     * @return
     */
    @RequestMapping(value = "/order-receive/update")
    public Result update(@RequestParam(required = true) Integer id,
                         @RequestParam(required = false) Integer techAuthId,
                         @RequestParam(required = false) BigDecimal price,
                         @RequestParam(required = false) Integer unitId) {
        if (new BigDecimal(Constant.DEF_RECEIVING_ORDER_PRICE).compareTo(price) > 0) {
            return Result.error().msg("接单价格不能低于" + Constant.DEF_RECEIVING_ORDER_PRICE + "元");
        }
        productService.update(id, techAuthId, price, unitId);
        return Result.success().msg("修改接单方式成功!");
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
    
    /**
     * 技能接单方式激活
     *
     * @param techAuthId
     * @param status
     * @return
     */
    @RequestMapping(value = "/order-receive/tech/enable")
    public Result techEnable(@RequestParam(required = true) Integer techAuthId,
                             @RequestParam(required = true) Boolean status) {
        User user = userService.getCurrentUser();
        productService.techEnable(techAuthId, status, user.getId());
        if (status) {
            return Result.success().msg("开启");
        } else {
            return Result.success().msg("关闭");
        }
    }

    /**
     * 用户所有接单方式列表
     * @return
     */
    @RequestMapping(value = "/order-receive/tech/list")
    public Result techList() {
        User user = userService.getCurrentUser();
        Map<String,Object> map = new HashMap<>();
        AssignOrderSettingVO assignOrderSettingVO = assignOrderSettingService.findByUserId(user.getId());
        List<TechAuthProductVO> techAuthProductVOS = productService.techAuthProductList(user.getId(), PlatformShowEnum.APP);
        map.put("assignOrderSetting",assignOrderSettingVO);
        map.put("techList",techAuthProductVOS);
        return Result.success().data(map);
    }
    
    
    /**
     * 查询用户商品详情页
     *
     * @param productId
     * @return
     */
    @RequestMapping(value = "/details")
    public Result findByProductId(Integer productId) {
        ProductDetailsVO serverCardVO = productService.findDetailsByProductId(productId);
        return Result.success().data(serverCardVO);
    }


    /**
     * 查询简单的商品信息
     *
     * @param productId
     * @return
     */
    @RequestMapping(value = "/simple/details")
    public Result findSimpleProductByProductId(Integer productId) {
        SimpleProductVO simpleProductVO = productService.findSimpleProductByProductId(productId);
        return Result.success().data(simpleProductVO);
    }


    /**
     * 用户本周订单和本周收入
     *
     * @return
     */
    @RequestMapping(value = "/order-receive/info")
    public Result orderReceiveUserInfo() {
        User user = (User) SubjectUtil.getCurrentUser();
        BigDecimal weekIncome = moneyDetailsService.weekIncome(user.getId());
        int orderCount = orderService.weekOrderCount(user.getId());
        User serverUser = userService.findById(user.getId());
        BigDecimal scoreAvg = serverUser.getScoreAvg();
        Map<String, Object> result = new HashMap<>();
        result.put("weekIncome", weekIncome);
        result.put("weekOrderCount", orderCount);
        result.put("scoreAvg", scoreAvg);
        return Result.success().data(result);
    }


    /**
     * 即将废弃
     * 接单方式激活
     *
     * @return
     */
    @Deprecated
    @RequestMapping(value = "/order-receive/enable")
    public Result enable(@RequestParam(required = true) Integer id,
                         @RequestParam(required = true) Boolean status) {
        Product product = productService.findById(id);
        if (product == null) {
            throw new ProductException(ProductException.ExceptionCode.PRODUCT_REVIEW_ING);
        }
        User user = userService.getCurrentUser();
        productService.enable(product, status, user.getId());
        if (status) {
            return Result.success().msg("开启");
        } else {
            return Result.success().msg("关闭");
        }
    }


    /**
     * 用户所有接单方式列表
     *
     * @return
     */
    @RequestMapping(value = "/order-receive/list")
    public Result list() {
        User user = userService.getCurrentUser();
        List<Product> productList = productService.findByUserId(user.getId());
        return Result.success().data(productList);
    }

    /**
     * 用户接单状态
     *
     * @return
     */
    @RequestMapping(value = "/order-receive/status")
    public Result orderReceiveStatus() {
        Map<String, Object> status = productService.readOrderReceivingStatus();
        status.put("CURRENT_TIME", new Date());
        return Result.success().data(status);
    }

    /**
     * 开始接单
     *
     * @return
     */
    @RequestMapping(value = "/order-receive/start")
    public Result orderReceiveStart(Float hour) {
        User user = userService.getCurrentUser();
        log.info("用户开始接单:userId:{};hour:{};", user.getId(), hour);
        userService.isCurrentUser(user.getId());
        productService.startOrderReceiving(hour, user.getId());
        return Result.success().data("已经开始自动接单!");
    }

    /**
     * 停止接单
     *
     * @return
     */
    @RequestMapping(value = "/order-receive/stop")
    public Result orderReceiveStop() {
        User user = userService.getCurrentUser();
        productService.stopOrderReceiving(user.getId());
        return Result.success().data("已经停止自动接单!");
    }

    /**
     * 分页查询所有商品
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/recommend/list")
    public Result getRecommendList(@RequestParam(required = true) Integer pageNum,
                                   @RequestParam(required = true) Integer pageSize) {
        PageInfo<ProductShowCaseVO> pageInfo = productService.getRecommendList(pageNum, pageSize);
        return Result.success().data(pageInfo);
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
     * 设置技能为主要技能
     * @param techId
     * @return
     */
    @RequestMapping(value = "/order-receive/tech/main")
    public Result techMain(@RequestParam(required = true) Integer techId) {
        userTechAuthService.settingsTechMain(techId);
        return Result.success().msg("设置成功!");
    }


    @RequestMapping(value = "/order-receive/assign_order_sett/save")
    public Result assignOrder(AssignOrderSettingVO assignOrderSettingVO){
        User user = userService.getCurrentUser();
        assignOrderSettingVO.setUserId(user.getId());
        assignOrderSettingService.save(assignOrderSettingVO);
        return Result.success().data(assignOrderSettingVO).msg("设置成功！");
    }
    
}
