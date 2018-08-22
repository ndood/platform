package com.fulu.game.core.entity.vo;


import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fulu.game.core.entity.FenqileOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 分期乐订单拓展表
 *
 * @author Gong Zechun
 * @date 2018-08-14 18:19:17
 */
@Data
public class FenqileOrderVO extends FenqileOrder {

    /**
     * 老板昵称
     */
    private String userNickname;

    /**
     * 联系方式类型(1：手机号，2：QQ号，3：微信号)
     */
    private Integer contactType;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 老板手机号
     */
    private String userMobile;

    /**
     * 陪玩师昵称
     */
    private String serviceUserNickname;

    /**
     * 陪玩师手机号
     */
    private String serviceUserMobile;

    /**
     * 订单状态
     */
    @Excel(name = "订单状态", orderNum = "3", replace = {"订单关闭_100",
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
    private Integer orderStatus;

    /**
     * 商品名称
     */
    @Excel(name = "服务类型-技能", orderNum = "4", width = 15)
    private String productName;

    /**
     * 单价/单位/数量
     */
    @Excel(name = "单价/单位/数量", orderNum = "5", width = 15)
    private String priceUnitAmount;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer amount;

    /**
     * 单位
     */
    private String unit;

    /**
     * 优惠券金额
     */
    @Excel(name = "优惠券抵扣金额", orderNum = "6", width = 15)
    private BigDecimal couponMoney;

    /**
     * 应付金额
     */
    @Excel(name = "应付金额", orderNum = "7", width = 15)
    private BigDecimal payableMoney;

    /**
     * 实付金额
     */
    @Excel(name = "实付金额", orderNum = "8", width = 15)
    private BigDecimal actualMoney;

    /**
     * 订单总额
     */
    @Excel(name = "总金额", orderNum = "9", width = 15)
    private BigDecimal totalMoney;

    /**
     * 陪玩师金额
     */
    @Excel(name = "陪玩师金额", orderNum = "10", width = 15)
    private BigDecimal serverMoney;

    /**
     * 平台佣金
     */
    private BigDecimal commissionMoney;

    /**
     * 下单时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "下单时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "11", width = 15)
    private Date createTime;

    /**
     * 对账金额
     */
    @Excel(name = "对账金额", orderNum = "12", width = 15)
    private BigDecimal reconAmount;

    /**
     * 对账状态（0：未对账（默认）；1：已对账）
     */
    @Excel(name = "是否对账", orderNum = "14", replace = {"是_1", "否_0"}, width = 15)
    private Integer reconStatus;

    /**
     * 对账时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "对账时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "15", width = 15)
    private Date processTime;

    /**
     * 对账人id
     */
    private Integer adminId;

    /**
     * 对账人用户名
     */
    @Excel(name = "对账人", orderNum = "16", width = 15)
    private String adminName;

    /**
     * 累计对账金额（未对账+已对账）
     */
    private BigDecimal totalAmount;

    /**
     * 累计未对账金额
     */
    private BigDecimal unReconTotalAmount;

    /**
     * 未对账的订单数量
     */
    private Integer unReconCount;

    /**
     * 分期乐平台佣金
     */
    @Excel(name = "对方佣金", orderNum = "13", width = 15)
    private BigDecimal fenqileCommissionMoney;

    /**
     * 订单状态位（文字描述）
     */
    private String statusStr;

    public String getPriceUnitAmount() {
        return price + "/" + unit + '/' + amount;
    }
}