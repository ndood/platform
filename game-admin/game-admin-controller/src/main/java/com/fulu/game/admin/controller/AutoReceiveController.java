package com.fulu.game.admin.controller;


import com.fulu.game.common.Result;
import com.fulu.game.common.enums.SettingTypeEnum;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.AutoReceivingOrder;
import com.fulu.game.core.entity.Setting;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.AutoReceivingOrderService;
import com.fulu.game.core.service.SettingService;
import com.fulu.game.core.service.UserTechAuthService;
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
public class AutoReceiveController extends BaseController{

    @Autowired
    private SettingService settingService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private AutoReceivingOrderService autoReceivingOrderService;
    @Autowired
    private UserTechAuthService userTechAuthService;

    /**
     * 设置自动接单时间
     * @param minute
     * @return
     */
    @PostMapping(value = "setting/save")
    public Result settingAutoReceiveTime(@RequestParam(required = true) Integer minute){
        Admin admin = adminService.getCurrentUser();
        Setting setting = new Setting();
        setting.setType(SettingTypeEnum.AUTO_RECEIVE_ORDER_TIME.getType());
        setting.setVal(String.valueOf(minute));
        setting.setAdminId(admin.getId());
        setting.setAdminName(admin.getName());
        setting.setCreateTime(new Date());
        return Result.success().data(setting);
    }


    /**
     * tech
     * @param techAuthId
     * @return
     */
    @PostMapping(value = "add/tech")
    public Result addTech(@RequestParam(required = true)Integer techAuthId,
                          String remark){
        UserTechAuth userTechAuth = userTechAuthService.findById(techAuthId);
        Admin admin = adminService.getCurrentUser();

        AutoReceivingOrder autoReceivingOrder = new AutoReceivingOrder();
        autoReceivingOrder.setCategoryId(userTechAuth.getCategoryId());

        return Result.success();
    }


}




















