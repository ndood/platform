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

    //订单状态
    @Excel(name = "订单状态", orderNum = "2", replace = {"订单关闭_100",
            "订单关闭_101",
            "订单关闭_102",
            "订单关闭_110",
            "订单关闭_160",
            "待付款_200",
            "等待陪玩_210",
            "等待陪玩_213",
            "陪玩中_220",
            "等待验收_300",
            "协商中_350",
            "协商拒绝_352",
            "协商取消_354",
            "仲裁中_400",
            "仲裁中_401",
            "仲裁完成:老板胜诉_410",
            "协商完成_415",
            "协商完成_416",
            "仲裁完成:协商处理_420",
            "待评价_500",
            "待评价_501",
            "仲裁完成:陪玩师胜诉_502",
            "已评价_600"}, width = 15)
    private Integer status;


    private Date beginTime;



    //下单用户ID
    @Excel(name = "下单用户ID", orderNum = "7", width = 15)
    private Integer userId;
    //订单类型
    @Excel(name = "订单类型", orderNum = "3", replace = {"陪玩订单_1", "上分订单_2", "分期乐订单_3"}, width = 10)
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


    private Integer platform;

    //实付金额
    @Excel(name = "实付金额", orderNum = "9", width = 15)
    private BigDecimal actualMoney;

    //优惠券金额
    @Excel(name = "优惠券金额", orderNum = "10", width = 15)
    private BigDecimal couponMoney;
    //是否支付(1:已支付,2:未支付)
    @JsonIgnore
    @Excel(name = "支付状态", orderNum = "11", replace = {"已支付_true", "未支付_false"}, width = 15)
    private Boolean isPay;

    private Boolean isPayCallback;

    private Integer payment;  //支付方式

    //订单总额
    @Excel(name = "订单总额", orderNum = "12", width = 15)
    private BigDecimal totalMoney;
    //陪玩师金额
    @Excel(name = "支付陪玩师金额", orderNum = "13", width = 15)
    BigDecimal serverMoney;
    //佣金
    @Excel(name = "平台收入", orderNum = "14", width = 15)
    BigDecimal commissionMoney;
    //佣金比例
    private BigDecimal charges;
    //订单创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "创建时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "15", width = 35)
    private Date createTime;
    //
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    //订单支付时间
    @Excel(name = "支付时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "16", width = 35)
    private Date payTime;
    //订单完成时间
    @Excel(name = "完成时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "17", width = 35)
    private Date completeTime;
    @Excel(name = "接单时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "18", width = 35)
    private Date receivingTime;

    //联系方式类型(1：手机号，2：QQ号，3：微信号)
    @Excel(name = "联系方式类型", orderNum = "19", replace = {
            "手机号_1",
            "QQ号_2",
            "微信号_3",
            " _0",
            "_null"}, width = 15)
    private Integer contactType;

    //联系方式
    @Excel(name = "联系方式", orderNum = "20", width = 15)
    private String contactInfo;
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