package com.fulu.game.admin.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.OrderStatusGroupEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.entity.vo.responseVO.OrderResVO;
import com.fulu.game.core.entity.vo.searchVO.OrderSearchVO;
import com.fulu.game.core.service.OrderService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private OrderService orderService;

    /**
     * 管理员-订单列表
     *
     * @param orderSearchVO
     * @return
     */
    @RequestMapping("/list")
    public Result list(@RequestParam(required = true) Integer pageNum,
                       @RequestParam(required = true) Integer pageSize,
                       String orderBy,
                       OrderSearchVO orderSearchVO) {
        PageInfo<OrderResVO> orderList = orderService.list(orderSearchVO, pageNum, pageSize, orderBy);
        return Result.success().data(orderList).msg("查询列表成功！");
    }


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
     * 管理员强制完成订单,协商处理
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/admin/negotiate")
    public Result adminHandleNegotiateOrder(@RequestParam(required = true) String orderNo) {
        OrderVO orderVO = orderService.adminHandleNegotiateOrder(orderNo);
        return Result.success().data(orderVO.getOrderNo()).msg("订单完成,协商处理!");
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
        return Result.success().data(orderVO.getOrderNo()).msg("订单完成!");
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
        orderService.adminAppealOrder(orderNo, remark);
        return Result.success().msg("订单申诉成功!");
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
        List<Order> userList = orderService.findBySearchVO(orderSearchVO);
        ExportParams exportParams = new ExportParams(title, "sheet1", ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, Order.class, userList);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(title, "UTF-8"));
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
