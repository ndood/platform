package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.UserAutoReceiveOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * @author wangbin
 * @date 2018-07-26 19:42:13
 */
@Data
public class UserAutoReceiveOrderVO extends UserAutoReceiveOrder {

    /**
     * 最后登录时间（同User的loginTime属性）
     */
    private Date loginTime;

    /**
     * 用户总积分（同User的userScore属性）
     */
    private Integer userScore;

    /**
     * 游戏名称（同Category的name属性）
     */
    private String name;

    /**
     * 进行中的上分订单数量
     */
    private Integer runningOrderNum;

    /**
     * 上分订单失败率
     */
    private BigDecimal orderFailureRate;
}
