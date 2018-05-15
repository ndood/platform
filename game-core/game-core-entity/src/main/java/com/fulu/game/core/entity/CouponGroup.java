package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 优惠券组表
 * 
 * @author wangbin
 * @date 2018-05-15 10:41:12
 */
@Data
public class CouponGroup implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//减额
	@NotNull(message = "减额字段不能为空")
	private BigDecimal deduction;
	//是否是新用户专享
	private Boolean isNewUser;
	//生成数量
	private Integer amount;
	//兑换码
	private String redeemCode;
	//备注
	private String remark;
	//有效期开始时间
	private Date startUsefulTime;
	//有效期结束时间
	private Date endUsefulTime;
	//创建时间
	private Date createTime;

}
