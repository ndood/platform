package com.fulu.game.core.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 虚拟币和余额充值订单表
 *
 * @author Gong Zechun
 * @date 2018-09-03 16:10:17
 */
@Data
public class VirtualPayOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    @Excel(name = "充值id", orderNum = "1", width = 15)
    private Integer id;
    //订单号
    @Excel(name = "订单编号", orderNum = "9", width = 20)
    private String orderNo;
    //订单名称
    private String name;
    //用户id
    private Integer userId;
    //订单类型(1：虚拟币充值订单，2：余额充值订单)
    private Integer type;
    //支付方式（1：微信支付；2：余额支付）
    @Excel(name = "支付方式", orderNum = "8", replace = {"微信支付_1", "余额支付_2"}, width = 15)
    private Integer payment;
    //充值路径(1：开黑陪玩；2：开黑上分；3：微信公众号； 4：IOS； 5：ANDROID； 45:APP(android+ios))
    @Excel(name = "充值路径", orderNum = "7", replace = {
            "开黑陪玩_1",
            "开黑上分_2",
            "微信公众号_3",
            "IOS_4",
            "ANDROID_5",
            "APP(android+ios)_45"}, width = 15)
    private Integer payPath;
    //实付金额
    private BigDecimal actualMoney;
    //虚拟商品价格（对应钻石数量）
    private Long virtualMoney;
    //充值到平台的金额
    @Excel(name = "充值金额", orderNum = "6", width = 15)
    private BigDecimal money;
    //下单IP
    private String orderIp;
    //是否接收过微信支付回调(1:已接收,0:未接收)
    private Boolean isPayCallback;
    //订单支付时间
    @Excel(name = "充值时间", orderNum = "2", exportFormat = "yyyy-MM-dd HH:mm:ss", width = 15)
    private Date payTime;
    //更新时间
    private Date updateTime;
    //订单创建时间
    private Date createTime;

}
