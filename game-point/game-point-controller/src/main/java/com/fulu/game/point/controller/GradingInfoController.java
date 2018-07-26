package com.fulu.game.point.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.TechValue;
import com.fulu.game.core.entity.vo.GradingPriceVO;
import com.fulu.game.core.service.GradingPriceService;
import com.fulu.game.core.service.TechValueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/api/v1/grading")
public class GradingInfoController extends BaseController{

    @Autowired
    private TechValueService techValueService;

    @Autowired
    private GradingPriceService gradingPriceService;

    /**
     * 大区
     * @param categoryId
     * @return
     */
    @PostMapping(value = "/area")
    public Result gradingAreaInfo(Integer categoryId){
        List<TechValue> list = techValueService.areaList(categoryId);
        return Result.success().data(list);
    }


    /**
     * 段位价格
     * @param categoryId
     * @param type
     * @return
     */
    @PostMapping(value = "/price")
    public Result gradingInfo(Integer categoryId,
                              Integer type){
        List<GradingPriceVO> list = gradingPriceService.findByCategoryAndType(categoryId,type);
        return Result.success().data(list);
    }





}
