package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 订单技能关联表
 * 
 * @author yanbiao
 * @date 2018-04-25 18:27:54
 */
@Data
public class OrderProduct implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//商品名称
	private String productName;
	//关联商品
	private Integer productId;
	//订单号
	private String orderNo;
	//数量
	private Integer amount;
	//
	private BigDecimal price;
	//
	private Date createTime;
	//
	private Date updateTime;

}
