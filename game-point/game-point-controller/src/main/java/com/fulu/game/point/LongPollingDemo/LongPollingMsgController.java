package com.fulu.game.point.LongPollingDemo;

import com.fulu.game.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * 长轮询Controller
 *
 * @author Gong ZeChun
 * @date 2018/8/6 11:48
 */
@RestController
@RequestMapping("/api/v1/order")
@Slf4j
public class LongPollingMsgController {

    @Autowired
    private LongPollingMsgServiceImpl longPollingMsgService;

    /**
     * 获取陪玩师是否已接单状态
     *
     * @return
     */
    @PostMapping("/accept-order/status")
    public DeferredResult<Result> getServiceUserAcceptOrderStatus() {
        DeferredResult<Result> resultDeferredResult = longPollingMsgService.getServiceUserAcceptOrderStatus();
        return resultDeferredResult;
    }

//    /**
//     * 模拟陪玩师接单接口
//     *
//     * @return
//     */
//    @PostMapping("/mock")
//    public Result mockServiceUserAcceptOrder() {
//        //业务逻辑-略
//        Integer userId = 35;
//        Order order = new Order();
//        order.setOrderNo("180507430132");
//        order.setServiceUserId(111);
//        Constant.serviceUserAcceptOrderMap.put(userId, order);
//        return Result.success().msg("模拟成功！");
//    }
}
