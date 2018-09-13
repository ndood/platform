package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.CouponGroup;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 优惠券组表
 *
 * @author wangbin
 * @date 2018-05-15 10:41:12
 */
@Data
public class CouponGroupVO  extends CouponGroup {

    //减额
    @NotNull(message = "[减额]字段不能为空")
    @DecimalMax(value = "1000",message = "减额不能超过1000")
    private BigDecimal deduction;
    //是否是新用户专享
    @NotNull(message = "[新用户专享]字段不能为空")
    private Boolean isNewUser;
    //生成数量
    @NotNull(message = "[生成数量]字段不能为空")
    @Max(value = 100000,message = "发放数量不能超过十万")
    private Integer amount;

    //类型(1满减，2折扣)
    @NotNull(message = "[优惠券类型]字段不能为空")
    private Integer type;

    //兑换码
    @NotNull(message = "[兑换码]字段不能为空")
    private String redeemCode;
    //备注
    private String remark;
    //有效期开始时间
    @NotNull(message = "[有效期开始时间]字段不能为空")
    private Date startUsefulTime;
    //有效期结束时间
    @NotNull(message = "[有效期结束时间]字段不能为空")
    private Date endUsefulTime;


}
