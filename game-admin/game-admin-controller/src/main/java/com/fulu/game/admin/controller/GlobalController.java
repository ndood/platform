package com.fulu.game.admin.controller;


import com.fulu.game.common.Result;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.core.entity.Product;
import com.fulu.game.core.service.ConversionRateService;
import com.fulu.game.core.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.ParseException;

@RestController
@RequestMapping(value = "/api/v1/global")
@Slf4j
public class GlobalController extends BaseController{

    @Autowired
    private OssUtil ossUtil;
    @Autowired
    private ProductService productService;

    @Autowired
    private ConversionRateService conversionRateService;


    @PostMapping(value = "upload")
    public Result upload(@RequestParam("file") MultipartFile file,String name)throws Exception{
        String fileName =  ossUtil.uploadFile(file.getInputStream(),file.getOriginalFilename());
        return Result.success().data(fileName).msg(name);
    }



    @PostMapping("/bath/updateindex")
    public Result bathUpdateIndex(){
        productService.bathUpdateProductIndex();
        return Result.success().msg("批量更新所有商品索引完成!");
    }

    @PostMapping("/bath/delindex")
    public Result allIndexDel(){
        productService.deleteAllProductIndex();
        return Result.success().msg("删除所有索引完成!");
    }

    @PostMapping("/test/conversion")
    public Result testConversion(){
        try {
            conversionRateService.statisticsConversionRate();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Result.success().msg("手动测试转化率成功");
    }

}
