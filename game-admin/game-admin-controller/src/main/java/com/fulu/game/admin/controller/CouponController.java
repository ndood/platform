package com.fulu.game.admin.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Coupon;
import com.fulu.game.core.entity.CouponGrantUser;
import com.fulu.game.core.entity.CouponGroup;
import com.fulu.game.core.entity.vo.CouponGroupVO;
import com.fulu.game.core.service.CouponGrantService;
import com.fulu.game.core.service.CouponGrantUserService;
import com.fulu.game.core.service.CouponGroupService;
import com.fulu.game.core.service.CouponService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/coupon")
public class CouponController extends BaseController {

    @Autowired
    private CouponGroupService couponGroupService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponGrantService couponGrantService;
    @Autowired
    private CouponGrantUserService couponGrantUserService;


    /**
     * 生成优惠券
     *
     * @param couponGroup
     * @return
     */
    @PostMapping(value = "/generate")
    public Result generate(@Valid CouponGroupVO couponGroup) {
        CouponGroup oldCouponGroup = couponGroupService.findByRedeemCode(couponGroup.getRedeemCode());
        if (oldCouponGroup != null) {
            return Result.error().msg("该优惠劵兑换码已存在!");
        }
        couponGroupService.create(couponGroup);
        return Result.success().msg("生成优惠券成功!");
    }

    /**
     * 优惠券导出
     *
     * @param response
     * @param couponGroupId
     * @throws Exception
     */
    @RequestMapping("export/{couponGroupId}")
    public void export(HttpServletResponse response,
                       @PathVariable(name = "couponGroupId", required = true) Integer couponGroupId) throws Exception {
        //模拟从数据库获取需要导出的数据
        List<Coupon> couponList = couponService.findByCouponGroup(couponGroupId);
        ExportParams exportParams = new ExportParams("优惠券使用明细", "sheet1", ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, Coupon.class, couponList);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("优惠券使用明细", "UTF-8"));
        workbook.write(response.getOutputStream());
    }

    /**
     * 优惠券列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/list")
    public Result list(Integer pageNum,
                       Integer pageSize) {
        PageInfo<CouponGroup> pageInfo = couponGroupService.list(pageNum, pageSize, null);
        return Result.success().data(pageInfo);
    }

    /**
     * 优惠券统计
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/statistics")
    public Result statistics(Integer id) {
        Integer receiveCount = couponService.countByCouponGroup(id);
        Integer firstReceiveCount = couponService.countByCouponGroupAndIsFirst(id);
        Map<String, Integer> result = new HashMap<>();
        result.put("receiveCount", receiveCount);
        result.put("firstReceiveCount", firstReceiveCount);
        return Result.success().data(result);
    }

    /**
     * 优惠券详情
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/details")
    public Result details(@RequestParam(required = true) Integer id,
                          @RequestParam(required = true) Integer pageNum,
                          @RequestParam(required = true) Integer pageSize) {
        PageInfo<Coupon> pageInfo = couponService.listByGroup(id, pageNum, pageSize, null);
        return Result.success().data(pageInfo);
    }

    /**
     * 发放优惠券
     *
     * @param redeemCode 优惠券兑换码
     * @param userIds    用户id（多个以英文逗号隔开）
     * @param remark     备注
     * @return 封装结果集
     */
    @PostMapping(value = "/grant")
    public Result couponGrant(@RequestParam String redeemCode,
                              @RequestParam String userIds,
                              @RequestParam String remark) {
        if (StringUtils.isBlank(userIds)) {
            return Result.error().msg("用户ID不能为空");
        }
        couponGrantService.create(redeemCode, userIds, remark);
        return Result.success().msg("优惠券发放完成，发放失败用户请查看明细!");
    }

    /**
     * 优惠券发放记录列表
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数据条数
     * @return 封装结果集
     */
    @PostMapping(value = "/grant/list")
    public Result couponGrantList(Integer pageNum,
                                  Integer pageSize) {
        PageInfo page = couponGrantService.list(pageNum, pageSize, null);
        return Result.success().data(page);
    }

    /**
     * 优惠券发放记录导出
     *
     * @param response
     * @param grantId
     * @throws Exception
     */
    @RequestMapping("/grant/export/{grantId}")
    public void grantExport(HttpServletResponse response,
                            @PathVariable(name = "grantId", required = true) Integer grantId) throws Exception {
        String title = "优惠券发放明细";
        List<CouponGrantUser> couponGrantUsers = couponGrantUserService.findByGrantId(grantId);
        ExportParams exportParams = new ExportParams(title, "sheet1", ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, CouponGrantUser.class, couponGrantUsers);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(title, "UTF-8"));
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * 优惠券发放明细
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/grant/details")
    public Result couponGrantDetails(@RequestParam(required = true) Integer id,
                                     @RequestParam(required = true) Integer pageNum,
                                     @RequestParam(required = true) Integer pageSize) {
        PageInfo<CouponGrantUser> pageInfo = couponGrantUserService.list(id, pageNum, pageSize, null);
        return Result.success().data(pageInfo);
    }


}
