package com.fulu.game.play.schedule.task;

import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.OrderStatusGroupEnum;
import com.fulu.game.core.entity.FenqileReconciliation;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.vo.searchVO.OrderSearchVO;
import com.fulu.game.core.service.impl.FenqileReconciliationServiceImpl;
import com.fulu.game.schedule.service.impl.ScheduleOrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 分期乐订单对账任务
 *
 * @author Gong ZeChun
 * @date 2018/8/15 17:54
 */
@Component
@Slf4j
public class FenqileOrderReconTask {
    private final ScheduleOrderServiceImpl orderService;
    private final FenqileReconciliationServiceImpl fenqileReconciliationService;

    @Autowired
    public FenqileOrderReconTask(ScheduleOrderServiceImpl orderService,
                                 FenqileReconciliationServiceImpl fenqileReconciliationService) {
        this.orderService = orderService;
        this.fenqileReconciliationService = fenqileReconciliationService;
    }

    /**
     * 每天23:00将处于对账状态的订单同步到分期乐对账表
     */
    @Scheduled(cron = "0 0 23 * * ? ")
    public void synchronizeDayFenqileOrder() {
        OrderSearchVO orderSearchVO = new OrderSearchVO();
        orderSearchVO.setStatusList(OrderStatusGroupEnum.RECON_ALL.getStatusList());
        List<Order> orderList = orderService.findDayReconOrders(orderSearchVO);

        if (CollectionUtils.isNotEmpty(orderList)) {
            for (Order meta : orderList) {
                FenqileReconciliation temp = fenqileReconciliationService.findByOrderNo(meta.getOrderNo());
                if (temp == null) {
                    FenqileReconciliation reconciliation = new FenqileReconciliation();
                    reconciliation.setOrderNo(meta.getOrderNo());
                    reconciliation.setStatus(Constant.UN_RECON);
                    reconciliation.setAmount(meta.getActualMoney());
                    reconciliation.setCommissionMoney(null);
                    reconciliation.setUpdateTime(DateUtil.date());
                    reconciliation.setCreateTime(DateUtil.date());
                    fenqileReconciliationService.create(reconciliation);
                }
            }
        }
    }
}
