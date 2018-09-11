package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
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
	private BigDecimal deduction;
	//是否是新用户专享
	private Boolean isNewUser;
	//生成数量
	private Integer amount;
	//兑换码
	private String redeemCode;
	//备注
	private String remark;
	//限品类(为空则为全品类)
	private Integer categoryId;
	//类型(1满减，2折扣)
	private Integer type;
	//多少金额可用（为0则为无门槛）
	private BigDecimal fullReduction;


	//有效期开始时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date startUsefulTime;
	//有效期结束时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date endUsefulTime;

	private Integer adminId;

	private String adminName;
	//创建时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;


}
