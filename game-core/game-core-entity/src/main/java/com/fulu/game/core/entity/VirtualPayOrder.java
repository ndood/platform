package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 虚拟货币充值订单表
 * 
 * @author Gong Zechun
 * @date 2018-09-03 16:10:17
 */
@Data
public class VirtualPayOrder implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//订单号
	private String orderNo;
	//用户id
	private Integer userId;
	//实付金额
	private BigDecimal actualMoney;
	//虚拟商品价格（对应钻石数量）
	private Integer virtualMoney;
	//是否接收过微信支付回调(1:已接收,0:未接收)
	private Integer isPayCallback;
	//更新时间
	private Date updateTime;
	//订单创建时间
	private Date createTime;

}
