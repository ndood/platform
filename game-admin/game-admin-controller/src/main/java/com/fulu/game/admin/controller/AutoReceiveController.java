package com.fulu.game.admin.controller;


import com.fulu.game.common.Result;
import com.fulu.game.common.enums.SettingTypeEnum;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.Setting;
import com.fulu.game.core.entity.vo.UserAutoReceiveOrderVO;
import com.fulu.game.core.entity.vo.searchVO.UserInfoAuthSearchVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/api/v1/auto-receive")
public class AutoReceiveController extends BaseController {


    @Autowired
    private AdminService adminService;
    @Autowired
    private UserAutoReceiveOrderService userAutoReceiveOrderService;
    @Autowired
    private OrderService orderService;


    /**
     * 设置自动接单时间
     *
     * @param minute
     * @return
     */
    @PostMapping(value = "setting/save")
    public Result settingAutoReceiveTime(@RequestParam(required = true) Integer minute) {
        Admin admin = adminService.getCurrentUser();
        Setting setting = new Setting();
        setting.setType(SettingTypeEnum.AUTO_RECEIVE_ORDER_TIME.getType());
        setting.setVal(String.valueOf(minute));
        setting.setAdminId(admin.getId());
        setting.setAdminName(admin.getName());
        setting.setCreateTime(new Date());
        return Result.success().msg("设置自动接单时间成功!");
    }


    /**
     * tech
     *
     * @param techAuthId
     * @return
     */
    @PostMapping(value = "tech/add")
    public Result addTech(@RequestParam(required = true) Integer techAuthId,
                          String remark) {
        userAutoReceiveOrderService.addAutoReceivingTech(techAuthId, remark);
        return Result.success();
    }

    /**
     * 获取自动接单陪玩师列表
     *
     * @param pageNum              页码
     * @param pageSize             每页显示数据条数
     * @param userInfoAuthSearchVO 查询VO
     * @return 封装结果集
     */
    @PostMapping("/info-auth/list")
    public Result autoReceiveUserInfoAuthList(@RequestParam("pageNum") Integer pageNum,
                                              @RequestParam("pageSize") Integer pageSize,
                                              UserInfoAuthSearchVO userInfoAuthSearchVO) {
        PageInfo<UserAutoReceiveOrderVO> voList = userAutoReceiveOrderService.autoReceiveUserInfoAuthList(pageNum,
                pageSize,
                userInfoAuthSearchVO);
        if (voList == null) {
            return Result.error().msg("无数据！");
        }
        return Result.success().data(voList).msg("查询成功！");
    }

    /**
     * 获取进行中的上分订单数量和上分订单失败率
     * @param userId     用户id
     * @param categoryId 分类id
     * @return 封装结果集
     */
    @PostMapping("/dynamic/order-info/get")
    public Result getDynamicOrderInfo(@RequestParam Integer userId, @RequestParam Integer categoryId) {
        UserAutoReceiveOrderVO vo = orderService.getDynamicOrderInfo(userId, categoryId);
        if (vo == null) {
            return Result.error().msg("无数据！");
        }
        return Result.success().data(vo).msg("查询成功！");
    }


}




















