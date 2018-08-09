package com.fulu.game.core.service.impl.push;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public abstract class PushServiceImpl implements PushService {

    /**
     * 陪玩师开始服务
     *
     * @param order
     * @return
     */
    protected abstract void start(Order order);

    /**
     * 申请协商处理
     *
     * @param order
     */
    protected abstract void consult(Order order);

    /**
     * 拒绝协商处理
     *
     * @param order
     */
    protected abstract void rejectConsult(Order order);

    /**
     * 同意协商处理
     *
     * @param order
     */
    protected abstract void agreeConsult(Order order);

    /**
     * 取消协商处理
     *
     * @param order
     */
    protected abstract void cancelConsult(Order order);

    /**
     * 陪玩师取消订单，因为太忙
     *
     * @param order
     */
    protected abstract void cancelOrderByServer(Order order);

    /**
     * 用户取消订单
     *
     * @param order
     */
    protected abstract void cancelOrderByUser(Order order);

    /**
     * 陪玩师申请仲裁
     *
     * @param order
     */
    protected abstract void appealByServer(Order order);

    /**
     * 用户申请仲裁
     *
     * @param order
     */
    protected abstract void appealByUser(Order order);

    /**
     * 陪玩师提交验收订单
     *
     * @param order
     */
    protected abstract void checkOrder(Order order);

    /**
     * 用户验收订单
     *
     * @param order
     */
    protected abstract void acceptOrder(Order order);

}
