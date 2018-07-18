package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 
 * 
 * @author wangbin
 * @date 2018-07-18 15:40:34
 */
@Data
public class OrderConsult implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//订单号
	private String orderNo;
	//协商前订单状态
	private Integer orderStatus;
	//用户ID
	private Integer userId;
	//陪玩师ID
	private Integer serviceUserId;
	//1申诉，2仲裁
	private Integer type;
	//退款
	private BigDecimal refundMoney;
	//
	private Date createTime;
	//
	private Boolean isDel;

}
