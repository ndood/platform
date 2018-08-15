package com.fulu.game.core.entity.vo;


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
    private Integer orderStatus;

    /**
     * 商品名称
     */
    private String productName;

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
    private BigDecimal couponMoney;

    /**
     * 实付金额
     */
    private BigDecimal actualMoney;

    /**
     * 订单总额
     */
    private BigDecimal totalMoney;

    /**
     * 陪玩师金额
     */
    private BigDecimal serverMoney;

    /**
     * 平台佣金
     */
    private BigDecimal commissionMoney;

    /**
     * 下单时间
     */
    private Date createTime;

    /**
     * 对账金额
     */
    private BigDecimal reconAmount;

    /**
     * 对账状态（0：未对账（默认）；1：已对账）
     */
    private Integer status;

    /**
     * 对账时间
     */
    private Date processTime;

    /**
     * 对账人id
     */
    private Integer adminId;

    /**
     * 对账人用户名
     */
    private String adminName;

    /**
     * 累计对账金额（未对账+已对账）
     */
    private BigDecimal totalAmount;

    /**
     * 累计未对账金额
     */
    private BigDecimal UnReconTotalAmount;

    /**
     * 未对账的订单数量
     */
    private Integer unReconCount;

    /**
     * 分期乐平台佣金
     */
    private BigDecimal fenqileCommissionMoney;
}