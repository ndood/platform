package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 集市订单商品表
 * 
 * @author wangbin
 * @date 2018-06-13 17:28:55
 */
@Data
public class OrderMarketProduct implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//订单号
	private String orderNo;
	//游戏分类ID
	private Integer categoryId;
	//商品名称
	private String productName;
	//手机号
	private String mobile;
	//角色名
	private String rolename;
	//价格
	private BigDecimal price;
	//数量
	private Integer amount;
	//创建时间
	private Date createTime;
	//
	private Date updateTime;

}
