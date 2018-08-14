package com.fulu.game.core.entity.vo;


import cn.afterturn.easypoi.excel.annotation.Excel;
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
    @Excel(name = "最后登录时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "3", width = 15, height = 15)
    private Date loginTime;

    /**
     * 用户总积分（同User的userScore属性）
     */
    @Excel(name = "信誉积分", orderNum = "4", width = 15)
    private Integer userScore;

    /**
     * 游戏名称（同Category的name属性）
     */
    @Excel(name = "上分游戏", orderNum = "5", width = 15)
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
