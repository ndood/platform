package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


/**
 * 优惠券表
 * 
 * @author wangbin
 * @date 2018-05-15 17:22:33
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
	private Boolean isNewUser;
	//绑定了那个用户
	private Integer userId;
	//领取手机号
	private String mobile;
	//是否被使用(0:否,1:是)
	private Boolean isUse;
	//订单号
	private String orderNo;
	//有效期开始时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date startUsefulTime;
	//有效期结束时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date endUsefulTime;
	//领取时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date receiveTime;
	//使用时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date useTime;
	//领取IP
	private String receiveIp;
	//使用IP
	private String useIp;
	//
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;

}
