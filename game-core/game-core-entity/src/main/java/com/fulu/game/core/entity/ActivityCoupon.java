package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 活动优惠券表
 * 
 * @author wangbin
 * @date 2018-09-28 16:48:52
 */
@Data
public class ActivityCoupon implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//优惠券组ID
	private Integer couponGroupId;
	//官方活动ID
	private Integer officialActivityId;
	//优惠券类型(1满减，2折扣)
	private Integer couponType;
	//优惠券编码
	private String redeemCode;
	//减额金额
	private BigDecimal deduction;
	//分类ID
	private Integer categoryId;
	//游戏分类内容
	private String categoryName;
	//多少金额可用(0为无门槛)
	private BigDecimal fullReduction;
	//
	private Date createTime;
	//
	private Date updateTime;

}
