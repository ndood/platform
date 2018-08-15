package com.fulu.game.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 分期乐对账表
 *
 * @author Gong Zechun
 * @date 2018-08-14 18:24:00
 */
@Data
public class FenqileReconciliation implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    private Integer id;
    //订单编号
    private String orderNo;
    //对账状态（0：未对账（默认）；1：已对账）
    private Integer status;
    //对账金额
    private BigDecimal amount;
    //分期乐平台佣金
    private BigDecimal commissionMoney;
    //对账时间
    private Date processTime;
    //对账人id
    private Integer adminId;
    //对账人用户名
    private String adminName;
    //备注
    private String remark;
    //修改时间
    private Date updateTime;
    //创建时间
    private Date createTime;

}
