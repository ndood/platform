package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.VirtualPayOrder;
import lombok.Data;

import java.util.Date;


/**
 * 虚拟币和余额充值订单表
 *
 * @author Gong Zechun
 * @date 2018-09-03 16:10:17
 */
@Data
public class VirtualPayOrderVO extends VirtualPayOrder {
    /**
     * 申请人
     */
    private Integer userId;

    /**
     * 申请者手机号
     */
    private String mobile;

    /**
     * 申请者昵称
     */
    private String nickname;

    /**
     * 订单支付成功开始时间
     */
    private Date payStartTime;

    /**
     * 订单支付成功结束时间
     */
    private Date payEndTime;
}
