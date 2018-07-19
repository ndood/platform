package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;


/**
 * 订单事件表
 * 
 * @author wangbin
 * @date 2018-07-19 14:04:47
 */
@Data
public class OrderEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//订单号
	private String orderNo;
	//协商前订单状态
	@JsonIgnore
	private Integer orderStatus;
	//申请人ID
	private Integer applyId;
	//用户ID
	private Integer userId;
	//陪玩师ID
	private Integer serviceUserId;
	//1:仲裁，2:验收,3:协商
	private Integer type;
	//退款
	private BigDecimal refundMoney;
	//
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	//
	private Boolean isDel;

}
