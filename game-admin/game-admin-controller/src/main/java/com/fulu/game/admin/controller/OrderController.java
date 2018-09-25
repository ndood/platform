package com.fulu.game.admin.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.fulu.game.admin.service.impl.AdminOrderServiceImpl;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.OrderStatusGroupEnum;
import com.fulu.game.core.entity.ArbitrationDetails;
import com.fulu.game.core.entity.OrderAdminRemark;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.OrderDealVO;
import com.fulu.game.core.entity.vo.OrderStatusDetailsVO;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.entity.vo.responseVO.OrderResVO;
import com.fulu.game.core.entity.vo.searchVO.OrderSearchVO;
import com.fulu.game.core.service.OrderAdminRemarkService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/order")
public class OrderController extends BaseController {

    private final AdminOrderServiceImpl orderService;

    @Autowired
    public OrderController(AdminOrderServiceImpl orderService) {
        this.orderService = orderService;
    }
    
    @Autowired
    public OrderAdminRemarkService orderAdminRemarkService;
    

    /**
     * 管理员-订单列表
     *
     * @param pageNum       页码
     * @param pageSize      每页显示数据条数
     * @param orderBy       排序字符串
     * @param orderSearchVO 查询VO
     * @return 封装结果集
     */
    @RequestMapping("/list")
    public Result list(@RequestParam Integer pageNum,
                       @RequestParam Integer pageSize,
                       String orderBy,
                       OrderSearchVO orderSearchVO) {
        PageInfo<OrderResVO> orderList = orderService.list(orderSearchVO, pageNum, pageSize, orderBy);
        return Result.success().data(orderList).msg("查询列表成功！");
    }

    /**
     * 管理员-延迟未接订单列表（八分钟未接的订单）
     *
     * @param pageNum       页码
     * @param pageSize      每页显示数据条数
     * @param orderBy       排序字符串
     * @param orderSearchVO 查询VO
     * @return 封装结果集
     */
    @RequestMapping("/delay/list")
    public Result delayList(@RequestParam Integer pageNum,
                            @RequestParam Integer pageSize,
                            String orderBy,
                            OrderSearchVO orderSearchVO) {
        PageInfo<OrderResVO> orderList = orderService.delayList(orderSearchVO, pageNum, pageSize, orderBy);
        return Result.success().data(orderList).msg("查询列表成功！");
    }

    /**
     * 管理员-所有订单状态位
     *
     * @return 封装结果集
     */
    @RequestMapping("status-all")
    public Result statusList() {
        JSONArray ja = new JSONArray();
        for (OrderStatusGroupEnum groupEnum : OrderStatusGroupEnum.values()) {
            if ("ADMIN".equals(groupEnum.getType())) {
                JSONObject jo = new JSONObject();
                jo.put("status", groupEnum.getValue());
                jo.put("msg", groupEnum.getName());
                jo.put("name", groupEnum.name());
                ja.add(jo);
            }
        }
        return Result.success().data(ja);
    }

    /**
     * 管理员强制完成订单,协商处理(提交仲裁结果)
     *
     * @param details
     * @return
     */
    @RequestMapping(value = "/admin/negotiate")
    public Result adminHandleNegotiateOrder(ArbitrationDetails details) {
        OrderVO orderVO = orderService.adminHandleNegotiateOrder(details);
        return Result.success().data(orderVO.getOrderNo()).msg("仲裁已完成");
    }

    /**
     * 管理员强制完成订单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/admin/complete")
    public Result adminHandleCompleteOrder(@RequestParam(required = true) String orderNo) {
        OrderVO orderVO = orderService.adminHandleCompleteOrder(orderNo);
        return Result.success().data(orderVO.getOrderNo()).msg("订单完成,退款给陪玩师!");
    }

    /**
     * 管理员退款给用户
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/admin/refund")
    public Result adminHandleRefundOrder(@RequestParam(required = true) String orderNo) {
        OrderVO orderVO = orderService.adminHandleRefundOrder(orderNo);
        return Result.success().data(orderVO.getOrderNo()).msg("订单完成,退款给用户!");
    }

    /**
     * 管理员取消订单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/admin/cancel")
    public Result adminHandleCancelOrder(@RequestParam(required = true) String orderNo) {
        orderService.adminCancelOrder(orderNo);
        return Result.success().msg("取消订单成功!");
    }

    /**
     * 管理员申诉订单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/admin/appeal")
    public Result adminHandleAppealOrder(@RequestParam(required = true) String orderNo,
                                         @RequestParam(required = true) String remark) {
//        orderService.adminAppealOrder(orderNo, remark);
        return Result.error().msg("管理员不允许仲裁订单!");
    }

    /**
     * 管理员查看订单流程
     *
     * @param orderNo
     * @return
     */
    @PostMapping("/admin/order-process")
    public Result getOrderProcess(@RequestParam String orderNo) {
        List<OrderStatusDetailsVO> voList = orderService.getOrderProcess(orderNo);
        if (voList == null) {
            return Result.error().msg("无订单数据！");
        }
        return Result.success().data(voList).msg("订单流程查询成功!");
    }

    /**
     * 获取协商详情
     *
     * @param orderNo
     * @return
     */
    @PostMapping("/admin/consult-detail")
    public Result getConsultDetail(@RequestParam String orderNo) {
        List<OrderDealVO> voList = orderService.findOrderConsultEvent(orderNo);
        if (voList == null) {
            return Result.error().data(voList).msg("无协商详情数据！");
        }
        return Result.success().data(voList).msg("获取协商详情成功!");
    }

    /**
     * 获取仲裁详情
     *
     * @param orderNo
     * @return
     */
    @PostMapping("/admin/negotiate-detail")
    public Result getNegotiateDetail(@RequestParam String orderNo) {
        List<OrderDealVO> voList = orderService.findNegotiateEvent(orderNo);
        if (voList == null) {
            return Result.error().data(voList).msg("无仲裁详情数据！");
        }
        return Result.success().data(voList).msg("获取仲裁详情成功!");
    }

    /**
     * 订单列表导出
     *
     * @param response
     * @throws Exception
     */
    @RequestMapping("/export")
    public void orderExport(HttpServletResponse response,
                            OrderSearchVO orderSearchVO) throws Exception {
        String title = "订单列表";
        List<OrderResVO> orderResVOList = orderService.findBySearchVO(orderSearchVO);
        ExportParams exportParams = new ExportParams(title, "sheet1", ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, OrderResVO.class, orderResVOList);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(title, "UTF-8"));
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * 未接单订单列表
     *
     * @param pageNum       页码
     * @param pageSize      每页显示数据条数
     * @param orderSearchVO 查询VO
     * @return 封装结果集
     */
    @PostMapping("/unaccept/list")
    public Result unacceptOrderList(@RequestParam("pageNum") Integer pageNum,
                                    @RequestParam("pageSize") Integer pageSize,
                                    OrderSearchVO orderSearchVO) {
        PageInfo<OrderVO> orderVOPageInfo = orderService.unacceptOrderList(pageNum, pageSize, orderSearchVO);
        if (orderVOPageInfo == null) {
            return Result.error().msg("无数据！");
        }
        return Result.success().data(orderVOPageInfo).msg("查询成功！");
    }


    //管理员设置处理备注
    @RequestMapping("/set-order/remark")
    public Result setAdminOrderRemark(Integer orderId,Integer adminId,String adminName,String remark){

        OrderAdminRemark oar = new OrderAdminRemark();
        oar.setOrderId(orderId);
        oar.setAgentAdminId(adminId);
        oar.setAgentAdminName(adminName);
        oar.setRemark(remark);

        orderAdminRemarkService.saveAdminOrderRemark(oar);

        return Result.success().msg("操作成功");
    }
}
