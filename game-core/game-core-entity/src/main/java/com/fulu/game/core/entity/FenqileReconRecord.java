package com.fulu.game.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 分期乐对账记录表
 *
 * @author Gong Zechun
 * @date 2018-08-15 20:26:58
 */
@Data
public class FenqileReconRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    private Integer id;
    //订单开始时间
    private Date startTime;
    //订单结束时间
    private Date endTime;
    //对账金额
    private BigDecimal amount;
    //订单数量
    private Integer orderCount;
    //订单完成时间
    private Date orderCompleteTime;
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
