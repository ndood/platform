package com.fulu.game.point.LongPollingDemo;

import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.Map;

/**
 * 长轮询Controller
 *
 * @author Gong ZeChun
 * @date 2018/8/6 11:48
 */
@RestController
@RequestMapping("/long-polling/test")
@Slf4j
public class LongPollingMsgController {

    @Autowired
    private LongPollingMsgServiceImpl longPollingMsgService;
    @Autowired
    private UserService userService;

    /**
     * 获取陪玩师是否已接单状态
     *
     * @return
     */
    @PostMapping("/get")
    public DeferredResult<Result> getServiceUserAcceptOrderStatus() throws InterruptedException {
        User user = userService.getCurrentUser();
        Integer userId = user.getId();
        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("flag", 0);
        Result defaultResult = Result.success().data(resultMap).msg("5秒钟内没有陪玩师接单！");
        //超时时间:10秒
        DeferredResult deferredResult = new DeferredResult<>(10000L, defaultResult);

        Order order = new Order();
        //轮询 查找是否有陪玩师接单
        long startTimeMillis = System.currentTimeMillis();
        while(true) {
            if(Constant.serviceUserAcceptOrderMap.containsKey(userId)) {
                System.out.println("查询到有陪玩师接单！");
                order = (Order)Constant.serviceUserAcceptOrderMap.get(userId);
                resultMap.put("flag", 1);
                Result result = Result.success().data(resultMap).msg("陪玩师接单！");
                deferredResult.setResult(result);
                Constant.serviceUserAcceptOrderMap.remove(userId);
                return deferredResult;
            }
            Thread.sleep(1000);
            long currentTimeMillis = System.currentTimeMillis();
            long resultTimeMillis = currentTimeMillis - startTimeMillis;
            if(resultTimeMillis >= 10000L) {
                System.out.println("超时退出轮询");
                break;
            }
        }

        //当DeferedResult对象调用setResult之后，响应完毕客户端，则会调用onCompletion对应的方法
        Order finalOrder = order;
        deferredResult.onCompletion(
                () -> {
                    Object flag = deferredResult.getResult();
//                    if(flag) {
                        log.info("陪玩师xxx已接单，玩家userId为:{}", finalOrder.getId());
                        System.out.println("已响应完客户端操作！");
//                    }
                });
        return deferredResult;
    }

    /**
     * 模拟陪玩师接单接口
     *
     * @return
     */
    @PostMapping("/mock")
    public Result mockServiceUserAcceptOrder() {
        //业务逻辑-略
        Integer userId = 34;
        Order order = new Order();
        order.setOrderNo("180507430132");
        order.setServiceUserId(111);
        Constant.serviceUserAcceptOrderMap.put(userId, order);
        return Result.success().msg("模拟成功！");
    }
}
