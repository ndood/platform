package com.fulu.game.app.service.impl;

import com.fulu.game.common.enums.PaymentEnum;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.enums.VirtualMoneyPriceEnum;
import com.fulu.game.common.exception.PayException;
import com.fulu.game.core.entity.AppstorePayDetail;
import com.fulu.game.core.entity.VirtualPayOrder;
import com.fulu.game.core.service.AppstorePayDetailService;
import com.fulu.game.core.service.impl.VirtualPayOrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class AppVirtualPayOrderServiceImpl extends VirtualPayOrderServiceImpl {

    @Autowired
    private AppleStorePayService appleStorePayService;
    @Autowired
    private AppstorePayDetailService appstorePayDetailService;

    /**
     * 苹果内购充值钻石
     *
     * @param receiptData
     * @param userId
     * @param ip
     * @return
     */

    public Boolean iosChargeDiamond(String receiptData, int userId, String ip) {
        AppstorePayDetail detail = appleStorePayService.diamondPay(receiptData);
        if (detail == null) {
            return false;
        }
        if (appstorePayDetailService.findByTransactionId(detail.getTransactionId()) != null) {
            throw new PayException(PayException.ExceptionCode.REPETITION_PAY);
        }
        int virtualMoney = VirtualMoneyPriceEnum.getNumberByPriceStr(detail.getProductId());
        VirtualPayOrder virtualPayOrder = diamondCharge(userId, virtualMoney, PaymentEnum.APPLE_STORE_PAY.getType(), PlatformEcoEnum.IOS.getType(), ip);
        detail.setOrderNo(virtualPayOrder.getOrderNo());
        detail.setCreateDate(new Date());
        detail.setUpdateDate(new Date());
        appstorePayDetailService.create(detail);
        successPayOrder(virtualPayOrder.getOrderNo(), virtualPayOrder.getActualMoney());
        return true;
    }

}
