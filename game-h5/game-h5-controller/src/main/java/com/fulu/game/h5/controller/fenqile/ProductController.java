package com.fulu.game.h5.controller.fenqile;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.vo.ProductDetailsVO;
import com.fulu.game.core.entity.vo.SimpleProductVO;
import com.fulu.game.core.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品Controller
 *
 * @author Gong ZeChun
 * @date 2018/8/13 17:02
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/product")
public class ProductController extends BaseController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 查询用户商品详情页
     *
     * @param productId 商品id
     * @return 封装结果集
     */
    @RequestMapping(value = "/details")
    public Result findByProductId(Integer productId) {
        ProductDetailsVO serverCardVO = productService.findDetailsByProductId(productId);
        return Result.success().data(serverCardVO);
    }

    /**
     * 查询简单的商品信息
     *
     * @param productId 商品id
     * @return 封装结果集
     */
    @RequestMapping(value = "/simple/details")
    public Result findSimpleProductByProductId(Integer productId) {
        SimpleProductVO simpleProductVO = productService.findSimpleProductByProductId(productId);
        return Result.success().data(simpleProductVO);
    }
}
