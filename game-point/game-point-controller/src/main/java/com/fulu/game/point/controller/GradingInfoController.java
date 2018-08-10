package com.fulu.game.point.controller;

import cn.hutool.core.util.RandomUtil;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.PointTypeEnum;
import com.fulu.game.core.entity.*;
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
    @Autowired
    private UserInfoAuthService userInfoAuthService;


    /**
     * 获取用户推送时间间隔
     * @return
     */
    @PostMapping(value = "/setting/pushtime/get")
    public Result getPushTimeInterval(){
        User user = userService.getCurrentUser();
        UserInfoAuth userInfoAuth =  userInfoAuthService.findByUserId(user.getId());
        Float time = 0F;
        if(userInfoAuth.getPushTimeInterval()==null){
            time = 0F;
        }else{
            if(userInfoAuth.getPushTimeInterval()>0){
                time = 1F;
            }
        }
        return Result.success().data(time);
    }

    /**
     * 设置推送时间间隔
     * @return
     */
    @PostMapping(value = "/setting/pushtime")
    public Result settingPushTimeInterval(@RequestParam(required = true)Float minute){
        userInfoAuthService.settingPushTimeInterval(minute);
        return Result.success().msg("设置推送时间间隔成功!");
    }


    /**
     * 上分游戏类型
     * @return
     */
    @PostMapping(value = "/category")
    public Result gradingGames() {
        List<Category> categoryList = categoryService.findPointCategory();
        return Result.success().data(categoryList);
    }

    /**
     * 在线大神数量
     * @return
     */
    @PostMapping(value = "/online/num")
    public Result onlineServerNum() {
        int randomNum = RandomUtil.randomInt(0, 100);
        return Result.success().data(200+randomNum);
    }


    /**
     * 订单等待，陪玩师头像显示
     * @return
     */
    @PostMapping(value = "/server/head")
    public Result serverUserIconInfo() {
        List<String> userHeads = userAutoReceiveOrderService.findAllAutoOrderUserHead();
        return Result.success().data(userHeads);
    }



    /**
     * 大区
     *
     * @param categoryId
     * @return
     */
    @PostMapping(value = "/area")
    public Result gradingAreaInfo(@RequestParam(required = true) Integer categoryId) {
        List<TechValue> list = techValueService.areaList(categoryId);
        return Result.success().data(list);
    }


    /**
     * 段位价格
     *
     * @param categoryId
     * @param type
     * @return
     */
    @PostMapping(value = "/price")
    public Result gradingInfo(@RequestParam(required = true) Integer categoryId,
                              @RequestParam(required = true) Integer type) {
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
        userAutoReceiveOrderService.activateAutoOrder(user.getId(), flag);
        String msg = "开启自动接单成功";
        if (!flag) {
            msg = "关闭自动接单成功";
        }
        return Result.success().msg(msg);
    }

    /**
     * 开启自动接单状态
     *
     * @return
     */
    @PostMapping(value = "/auto-order/status")
    public Result autoOrderStatus() {
        User user = userService.getCurrentUser();
        List<UserAutoReceiveOrder> userAutoReceiveOrderList = userAutoReceiveOrderService.findByUserId(user.getId());
        if (userAutoReceiveOrderList.isEmpty()) {
            return Result.error().msg("没有自动接单权限");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("userAutoSetting", userAutoReceiveOrderList.get(0).getUserAutoSetting());
        return Result.success().data(map);
    }


    /**
     * 段位范围列表
     *
     * @param categoryId
     * @return
     */
    @PostMapping(value = "/auto-order/range-list")
    public Result autoOrderRangeList(@RequestParam(required = true) Integer categoryId) {
        List<GradingPriceVO> gradingPriceVOList = gradingPriceService.findByCategoryAndType(categoryId, PointTypeEnum.ACCURATE_SCORE.getType());
        return Result.success().data(gradingPriceVOList);
    }


    /**
     * 自动接单设置查询
     * @param categoryId
     * @return
     */
    @PostMapping(value = "/auto-order/query")
    public Result autoOrderRangeQuery(@RequestParam(required = true) Integer categoryId){
        User user = userService.getCurrentUser();
        UserAutoReceiveOrder userAutoReceiveOrder = userAutoReceiveOrderService.findByUserIdAndCategoryId(user.getId(), categoryId);
        return Result.success().data(userAutoReceiveOrder);
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
    public Result autoOrderRangeSave(@RequestParam(required = true) Integer categoryId,
                                     @RequestParam(required = true) Integer areaId,
                                     @RequestParam(required = true) Integer startRank,
                                     @RequestParam(required = true) Integer endRank,
                                     String rankIds ) {
        User user = userService.getCurrentUser();
        UserAutoReceiveOrder userAutoReceiveOrder = userAutoReceiveOrderService.findByUserIdAndCategoryId(user.getId(), categoryId);
        if(userAutoReceiveOrder==null){
            return Result.error().msg("没有开启自动接单权限!");
        }
        userAutoReceiveOrder.setAreaId(areaId);
        userAutoReceiveOrder.setStartRank(startRank);
        userAutoReceiveOrder.setRankIds(rankIds);
        userAutoReceiveOrder.setEndRank(endRank);
        userAutoReceiveOrder.setUpdateTime(new Date());
        userAutoReceiveOrderService.update(userAutoReceiveOrder);
        return Result.success().data(userAutoReceiveOrder).msg("接单方式保存成功!");
    }


}
