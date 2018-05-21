package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.Product;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.ProductDetailsVO;
import com.fulu.game.core.entity.vo.SimpleProductVO;
import com.fulu.game.core.service.MoneyDetailsService;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.ProductService;
import com.fulu.game.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
        productService.create(techAuthId, price, unitId);
        return Result.success().msg("添加接单方式成功!");
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
        productService.update(id, techAuthId, price, unitId);
        return Result.success().msg("修改接单方式成功!");
    }


    /**
     * 接单方式激活
     *
     * @return
     */
    @RequestMapping(value = "/order-receive/enable")
    public Result enable(@RequestParam(required = true) Integer id,
                         @RequestParam(required = true) Boolean status) {
        productService.enable(id, status);
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
        productService.startOrderReceiving(hour);
        return Result.success().data("已经开始自动接单!");
    }

    /**
     * 停止接单
     *
     * @return
     */
    @RequestMapping(value = "/order-receive/stop")
    public Result orderReceiveStop() {
        productService.stopOrderReceiving();
        return Result.success().data("已经停止自动接单!");
    }


}
