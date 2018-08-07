package com.fulu.game.point.LongPollingDemo;

import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.Map;

/**
 * todo：描述文字
 *
 * @author Gong ZeChun
 * @date 2018/8/6 11:53
 */
@Slf4j
@Service
public class LongPollingMsgServiceImpl {

    @Autowired
    private UserService userService;

    DeferredResult<Result> getServiceUserAcceptOrderStatus() {
        User user = userService.findById(36);
        if (user == null) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }

        Integer userId = user.getId();
        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("flag", Constant.SERVICE_USER_NOT_ACCEPT_ORDER);
        Result defaultResult = Result.success().data(resultMap).msg("60秒内没有陪玩师接单！");

        //超时时间:60秒
        DeferredResult<Result> deferredResult = new DeferredResult<>(Constant.MILLI_SECOND_60, defaultResult);

        Order order = new Order();

        long startTimeMillis = System.currentTimeMillis();
        while (true) {
            if (Constant.serviceUserAcceptOrderMap.containsKey(userId)) {
                order = (Order) Constant.serviceUserAcceptOrderMap.get(userId);
                log.info("陪玩师id:{}已接单，订单编号:{}！", order.getServiceUserId(), order.getOrderNo());
                resultMap.put("flag", Constant.SERVICE_USER_ACCEPT_ORDER);
                Result result = Result.success().data(resultMap).msg("陪玩师已接单！");
                deferredResult.setResult(result);
                Constant.serviceUserAcceptOrderMap.remove(userId);
                return deferredResult;
            }
            long currentTimeMillis = System.currentTimeMillis();
            long resultTimeMillis = currentTimeMillis - startTimeMillis;
            if (resultTimeMillis >= Constant.MILLI_SECOND_60) {
                System.out.println("超时退出轮询");
                break;
            }
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //响应完毕的日志
//        deferredResult.onCompletion(
//                () -> {
//                    if(!deferredResult.hasResult()) {
//                        Order order1 = (Order)Constant.serviceUserAcceptOrderMap.get(userId);
//                        log.info("订单:{}在60s内没有陪玩师接单！", order1.getOrderNo());
//                    }else {
//                        log.info("111");
//                    }
//                });
//
//        //超时日志
//        deferredResult.onTimeout(
//                () -> {
//                    Order order1 = orders;
//                    log.info("超时未接单，订单编号:{}", order1.getOrderNo());
//                });

        return deferredResult;
    }
}
