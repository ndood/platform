package com.fulu.game.core.entity.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fulu.game.core.entity.CashDraws;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author yanbiao
 * @date 2018-04-24 16:45:40
 */
@Data
public class CashDrawsVO extends CashDraws {

    /**
     * 申请开始时间
     */
    private Date createStartTime;

    /**
     * 申请结束时间
     */
    private Date createEndTime;

    /**
     * 提现申请的提醒文案
     */
    private String tips;

    /**
     * 认证的真实姓名
     */
    @Excel(name = "用户真实姓名", orderNum = "14", width = 15)
    private String realName;

    /**
     * 认证的身份证号
     */
    @Excel(name = "用户身份证号", orderNum = "15", width = 15)
    private String cardNo;

    /**
     * 用户OpenId
     */
    @Excel(name = "用户OpenId", orderNum = "16", width = 15)
    private String openId;

    /**
     * 用户魅力值
     */
    @Excel(name = "用户魅力值", orderNum = "17", width = 15)
    private BigDecimal charmMoney;

    /**
     * 用户可提现余额
     */
    @Excel(name = "用户余额", orderNum = "18", width = 15)
    private BigDecimal balance;

    /**
     * 加密字符串
     */
    private String sign;
}
