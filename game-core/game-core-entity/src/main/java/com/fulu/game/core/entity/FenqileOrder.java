package com.fulu.game.core.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 分期乐订单拓展表
 *
 * @author Gong Zechun
 * @date 2018-08-14 18:19:17
 */
@Data
public class FenqileOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    //
    private Integer id;
    /**
     * 订单号
     */
    @Excel(name = "订单号", orderNum = "1", width = 20)
    private String orderNo;

    /**
     * 分期乐支付编号
     */
    @Excel(name = "分期乐支付编号", orderNum = "2", width = 20)
    private String paymentNo;

    //备注
    private String remark;
    //修改时间
    private Date updateTime;
    //创建时间
    private Date createTime;

}
