package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;


/**
 * 订单表
 * 
 * @author yanbiao
 * @date 2018-04-25 18:27:54
 */
@Data
public class Order implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//订单号
	private String orderNo;
	//优惠券编码
	private String couponNo;
	//下单用户ID
	private Integer userId;
	//订单类型
	private Integer type;
	//渠道商ID
	private Integer channelId;
	//陪玩师用户ID
	private Integer serviceUserId;
	//订单游戏分类
	private Integer categoryId;
	//订单名称
	private String name;
	//备注
	private String remark;
	//订单状态
	private Integer status;
	//实付金额
	private BigDecimal actualMoney;
	//优惠券金额
	private BigDecimal couponMoney;
	//是否支付(1:已支付,2:未支付)
	@JsonIgnore
	private Boolean isPay;
	//佣金
	@JsonIgnore
	private BigDecimal commissionMoney;
	//订单总额
	private BigDecimal totalMoney;

	//订单创建时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;
	//
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date updateTime;
	//订单支付时间
	private Date payTime;
	//订单完成时间
	private Date completeTime;

	private Date receivingTime;

	//下单IP
	private String orderIp;


	public String getRemark() {
		if(null==remark){
			return "";
		}
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
