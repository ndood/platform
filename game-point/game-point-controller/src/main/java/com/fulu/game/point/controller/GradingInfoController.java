package com.fulu.game.point.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.PointTypeEnum;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.TechValue;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserAutoReceiveOrder;
import com.fulu.game.core.entity.vo.GradingPriceVO;
import com.fulu.game.core.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping(value = "/api/v1/grading")
public class GradingInfoController extends BaseController {

    @Autowired
    private TechValueService techValueService;
    @Autowired
    private GradingPriceService gradingPriceService;
    @Autowired
    private UserAutoReceiveOrderService userAutoReceiveOrderService;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;


    @PostMapping(value = "/category")
    public Result gradingGames(){
        List<Category> categoryList =  categoryService.findPointCategory();
        return Result.success().data(categoryList);
    }

    /**
     * 大区
     * @param categoryId
     * @return
     */
    @PostMapping(value = "/area")
    public Result gradingAreaInfo(Integer categoryId) {
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
                              Integer type) {
        List<GradingPriceVO> list = gradingPriceService.findByCategoryAndType(categoryId, type);
        return Result.success().data(list);
    }


    /**
     * 开启关闭自动接单
     * @return
     */
    @PostMapping(value = "/auto-order/switch")
    public Result autoOrderReceiving(@RequestParam(required = true) Boolean flag) {
        User user = userService.getCurrentUser();
        userAutoReceiveOrderService.activateAutoOrder(user.getId(),flag);
        String msg = "开启自动接单成功";
        if(!flag){
            msg = "关闭自动接单成功";
        }
        return Result.success().msg(msg);
    }

    /**
     * 开启自动接单状态
     * @return
     */
    @PostMapping(value = "/auto-order/status")
    public Result autoOrderStatus(){
        User user = userService.getCurrentUser();
        List<UserAutoReceiveOrder>  userAutoReceiveOrderList =userAutoReceiveOrderService.findByUserId(user.getId());
        if(userAutoReceiveOrderList.isEmpty()){
            return Result.error().msg("没有自动接单权限");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("userAutoSetting",userAutoReceiveOrderList.get(0).getUserAutoSetting());
        return Result.success().data(map);
    }


    /**
     * 段位范围列表
     * @param categoryId
     * @return
     */
    @PostMapping(value = "/auto-order/range-list")
    public Result autoOrderRangeList(Integer categoryId){
        List<GradingPriceVO> gradingPriceVOList =  gradingPriceService.findByCategoryAndType(categoryId, PointTypeEnum.ACCURATE_SCORE.getType());
        return Result.success().data(gradingPriceVOList);
    }

    /**
     * 保存段位和大区
     * @param categoryId
     * @param areaId
     * @param startRank
     * @param endRank
     * @return
     */
    @PostMapping(value = "/auto-order/range-save")
    public Result autoOrderRangeSave(Integer categoryId,
                                     Integer areaId,
                                     Integer startRank,
                                     Integer endRank){
        User user = userService.getCurrentUser();
        UserAutoReceiveOrder userAutoReceiveOrder =  userAutoReceiveOrderService.findByUserIdAndCategoryId(user.getId(),categoryId);
        userAutoReceiveOrder.setAreaId(areaId);
        userAutoReceiveOrder.setStartRank(startRank);
        userAutoReceiveOrder.setEndRank(endRank);
        userAutoReceiveOrder.setUpdateTime(new Date());
        userAutoReceiveOrderService.update(userAutoReceiveOrder);
        return Result.success().data(userAutoReceiveOrder);
    }




}
