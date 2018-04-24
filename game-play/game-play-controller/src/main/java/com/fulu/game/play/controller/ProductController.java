package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Product;
import com.fulu.game.core.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/product")
public class ProductController extends BaseController{

    @Autowired
    private ProductService productService;

    /**
     * 添加接单方式
     * @param techAuthId
     * @param price
     * @param unitId
     * @return
     */
    @RequestMapping(value = "create")
    public Result create(Integer techAuthId,
                         BigDecimal price,
                         Integer unitId){
        productService.create(techAuthId,price,unitId);
        return Result.success().msg("添加接单方式成功!");
    }




    /**
     * 用户所有接单方式列表
     * @return
     */
    @RequestMapping(value = "list")
    public Result list(){
        List<Product> productList = productService.findAll();
        return Result.success().data(productList);
    }

    /**
     * 开始接单
     * @return
     */
    @RequestMapping(value = "start")
    public Result start(int hour){

        return Result.success();
    }


}
