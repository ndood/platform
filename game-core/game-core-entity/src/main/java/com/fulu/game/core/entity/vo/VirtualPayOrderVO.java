package com.fulu.game.core.entity.vo;


import cn.afterturn.easypoi.excel.annotation.Excel;
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
    @Excel(name = "用户id", orderNum = "4", width = 15)
    private Integer userId;

    /**
     * 申请者手机号
     */
    @Excel(name = "手机号", orderNum = "5", width = 15)
    private String mobile;

    /**
     * 申请者昵称
     */
    @Excel(name = "昵称", orderNum = "3", width = 15)
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
