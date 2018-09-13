package com.fulu.game.app.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Product;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.ProductDetailsVO;
import com.fulu.game.core.service.ProductService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

}
