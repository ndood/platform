package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 优惠券表
 * 
 * @author wangbin
 * @date 2018-05-15 10:41:12
 */
@Data
public class Coupon implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//优惠券组ID
	private Integer couponGroupId;
	//面额
	private BigDecimal deduction;
	//是否是新用户专享
	private Integer isNewUser;
	//绑定了那个用户
	private Integer userId;
	//是否被使用(0:否,1:是)
	private Integer isUse;
	//订单号
	private String orderNo;
	//有效期开始时间
	private Date startUsefulTime;
	//有效期结束时间
	private Date endUsefulTime;
	//
	private Date createTime;

}
