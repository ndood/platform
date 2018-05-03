package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.vo.requestVO.OrderReqVO;
import com.fulu.game.core.entity.vo.responseVO.OrderResVO;
import com.fulu.game.core.service.OrderProductService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("api/v1/orderProduct")
public class OrderProductController extends BaseController{

    @Autowired
    private OrderProductService orderProductService;
    /**
     * 管理员-订单列表
     * @param orderReqVO
     * @return
     */
    @RequestMapping("/list")
    public Result list(@ModelAttribute OrderReqVO orderReqVO){
        PageInfo<OrderResVO> orderList = orderProductService.list(orderReqVO);
        return Result.success().data(orderList).msg("查询列表成功！");
    }
}
