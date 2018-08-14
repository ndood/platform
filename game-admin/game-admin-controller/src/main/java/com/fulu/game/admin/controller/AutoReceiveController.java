package com.fulu.game.admin.controller;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.SettingTypeEnum;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.Setting;
import com.fulu.game.core.entity.vo.UserAutoReceiveOrderVO;
import com.fulu.game.core.entity.vo.searchVO.UserInfoAuthSearchVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.SettingService;
import com.fulu.game.core.service.UserAutoReceiveOrderService;
import com.fulu.game.core.service.impl.order.AdminOrderServiceImpl;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/auto-receive")
public class AutoReceiveController extends BaseController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private UserAutoReceiveOrderService userAutoReceiveOrderService;
    @Autowired
    private AdminOrderServiceImpl orderService;
    @Autowired
    private SettingService settingService;


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
        settingService.create(setting);
        return Result.success().data(setting).msg("设置自动接单时间成功!");
    }


    @PostMapping(value = "setting/list")
    public Result autoReceiveTimeQueryList(@RequestParam("pageSize") Integer pageSize,
                                           @RequestParam("pageNum") Integer pageNum) {
        PageInfo<Setting> settingList = settingService.settingList(pageNum, pageSize, SettingTypeEnum.AUTO_RECEIVE_ORDER_TIME.getType());
        return Result.success().data(settingList);
    }

    /**
     * 设置自动接单技能
     *
     * @param techAuthId
     * @return
     */
    @PostMapping(value = "tech/add")
    public Result addTech(@RequestParam(required = true) Integer techAuthId,
                          String remark) {
        userAutoReceiveOrderService.addAutoReceivingTech(techAuthId, remark);
        return Result.success().msg("自动接单设置成功!");
    }


    @PostMapping(value = "tech/del")
    public Result delTech(@RequestParam(required = true) Integer techAuthId) {
        userAutoReceiveOrderService.delAutoReceivingTech(techAuthId);
        return Result.success().msg("取消自动接单成功!");
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
     *
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

    /**
     * 自动接单陪玩师列表导出
     *
     * @param response
     * @throws Exception
     */
    @RequestMapping("/export")
    public void orderExport(HttpServletResponse response,
                            UserInfoAuthSearchVO userInfoAuthSearchVO) throws Exception {
        String title = "自动接单陪玩师列表";
        List<UserAutoReceiveOrderVO> resultList = userAutoReceiveOrderService.autoReceiveUserInfoAuthListByVO(userInfoAuthSearchVO);
        ExportParams exportParams = new ExportParams(title, "sheet1", ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, UserAutoReceiveOrderVO.class, resultList);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(title, "UTF-8"));
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}




















