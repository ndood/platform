package com.fulu.game.app.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Product;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.ProductService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    @PostMapping(value = "/user/list")
    public Result productList(@RequestParam Integer pageNum,
                              @RequestParam Integer pageSize,
                              @RequestParam(required = false) Integer  userId) {
        if(userId == null || userId.intValue() < 0){
            User user = userService.getCurrentUser();
            userId = user.getId();
        }
        PageInfo<Product> pageInfo = productService.userProductList(pageNum,pageSize,userId);
        return Result.success().data(pageInfo);
    }

}
