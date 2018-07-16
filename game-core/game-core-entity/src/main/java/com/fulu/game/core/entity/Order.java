package com.fulu.game.core.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 订单表
 *
 * @author yanbiao
 * @date 2018-04-25 18:27:54
 */
@Data
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    //订单号
    @Excel(name = "订单号", orderNum = "0", width = 20)
    private String orderNo;
    //优惠券编码
    @Excel(name = "优惠券编码", orderNum = "1", width = 20)
    private String couponNo;
    //下单用户ID
    @Excel(name = "下单用户ID", orderNum = "7", width = 15)
    private Integer userId;
    //订单类型
    @Excel(name = "订单类型", orderNum = "3", replace = {"普通订单_1", "集市订单_2"}, width = 10)
    private Integer type;
    //渠道商ID
    @Excel(name = "渠道商ID", orderNum = "4", width = 10)
    private Integer channelId;
    //陪玩师用户ID
    @Excel(name = "陪玩师ID", orderNum = "5", width = 10)
    private Integer serviceUserId;


    //订单游戏分类
    private Integer categoryId;
    //订单名称
    @Excel(name = "订单名称", orderNum = "6", width = 20)
    private String name;
    //备注
    private String remark;
    //订单状态
    @Excel(name = "订单状态", orderNum = "2", replace = {"订单关闭_100",
            "订单关闭_101",
            "订单关闭_110",
            "订单关闭_160",
            "待付款_200",
            "等待陪玩_210",
            "陪玩中_220",
            "等待验收_300",
            "申诉中_400",
            "申诉中_401",
            "申诉：全额退款_410",
            "申诉：协商处理_420",
            "待评价_500",
            "待评价_501",
            "申诉：订单完成_502",
            "已评价_600"}, width = 15)
    private Integer status;

    //联系方式类型(1：手机号，2：QQ号，3：微信号)
    @Excel(name = "联系方式类型", orderNum = "18", width = 15)
    private Integer contactType;
    //联系方式
    @Excel(name = "联系方式", orderNum = "19", width = 15)
    private String contactInfo;

    //实付金额
    @Excel(name = "实付金额", orderNum = "8", width = 15)
    private BigDecimal actualMoney;

    @Excel(name = "支付陪玩师金额", orderNum = "9", width = 15)
    private BigDecimal serverMoney;

    //优惠券金额
    @Excel(name = "优惠券金额", orderNum = "10", width = 15)
    private BigDecimal couponMoney;
    //是否支付(1:已支付,2:未支付)
    @JsonIgnore
    @Excel(name = "支付状态", orderNum = "11", replace = {"已支付_true", "未支付_false"}, width = 15)
    private Boolean isPay;

    private Boolean isPayCallback;

    //佣金
    @JsonIgnore
    @Excel(name = "佣金", orderNum = "12", width = 15)
    private BigDecimal commissionMoney;
    //订单总额
    @Excel(name = "订单总额", orderNum = "13", width = 15)
    private BigDecimal totalMoney;
    //佣金比例
    private BigDecimal charges;
    //订单创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "创建时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "14", width = 35)
    private Date createTime;
    //
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    //订单支付时间
    @Excel(name = "支付时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "15", width = 35)
    private Date payTime;
    //订单完成时间
    @Excel(name = "完成时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "16", width = 35)
    private Date completeTime;
    @Excel(name = "接单时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "17", width = 35)
    private Date receivingTime;
    //下单IP
    private String orderIp;

    public String getRemark() {
        if (null == remark) {
            return "";
        }
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
