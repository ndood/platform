package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 优惠券发放记录
 * 
 * @author wangbin
 * @date 2018-05-15 18:29:14
 */
@Data
public class CouponGrant implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//优惠卷组ID
	private Integer couponGroupId;
	//发放原因
	private String remark;
	//优惠券兑换码
	private String redeemCode;
	//优惠券面额
	private BigDecimal deduction;
	//是否是新用户专享
	private Boolean isNewUser;
	//有效期开始时间
	private Date startUsefulTime;
	//有效期结束时间
	private Date endUsefulTime;
	//发放人ID
	private Integer adminId;
	//发放人名称
	private String adminName;
	//发放时间
	private Date createTime;

}
