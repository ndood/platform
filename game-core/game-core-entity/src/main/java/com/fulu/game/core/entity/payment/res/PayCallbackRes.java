package com.fulu.game.core.entity.payment.res;

import com.fulu.game.common.enums.PayBusinessEnum;
import lombok.Data;

//todo 支付宝和微信可能有各自的回调业务参数在我们自己的业务中要使用，假如有那么需要建各自的回调类
@Data
public class PayCallbackRes {





    //是否支付成功
    private boolean isSuccess;

    //支付订单号
    private String orderNO;

    //支付金额
    private String payMoney;



}
