package com.fulu.game.core.entity.vo;

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
    private String realName;

    /**
     * 认证的身份证号
     */
    private String cardNo;

    /**
     * 用户OpenId
     */
    private String openId;

    /**
     * 用户魅力值
     */
    private BigDecimal charm;

    /**
     * 用户可提现余额
     */
    private BigDecimal balance;
}
