package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 
 * 
 * @author wangbin
 * @date 2018-07-18 13:32:16
 */
@Data
public class OrderShareProfit implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//订单号
	private String orderNo;
	//陪玩师金额
	private BigDecimal serverMoney;
	//平台收入金额
	private BigDecimal commissionMoney;
	//退款金额
	private BigDecimal userMoney;
	//创建时间
	private Date createTime;
	//
	private Date updateTime;

}
