package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.OrderStatusGroupEnum;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.entity.vo.responseVO.OrderResVO;
import com.fulu.game.core.entity.vo.searchVO.OrderReqVO;
import com.fulu.game.core.service.OrderProductService;
import com.fulu.game.core.service.OrderService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderProductService orderProductService;

    /**
     * 管理员-订单列表
     *
     * @param orderReqVO
     * @return
     */
    @RequestMapping("/list")
    public Result list(Integer pageNum,
                       Integer pageSize,
                       String orderBy,
                       OrderReqVO orderReqVO) {
        PageInfo<OrderResVO> orderList = orderProductService.list(orderReqVO, pageNum, pageSize, orderBy);
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
    public Result adminHandleNegotiateOrder(String orderNo) {
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
    public Result adminHandleCompleteOrder(String orderNo) {
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
    public Result adminHandleRefundOrder(String orderNo) {
        OrderVO orderVO = orderService.adminHandleRefundOrder(orderNo);
        return Result.success().data(orderVO.getOrderNo()).msg("订单完成,退款给用户!");
    }
}
