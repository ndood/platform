package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

import javax.validation.constraints.NotNull;


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

	private String type;
	//游戏分类ID
	@NotNull(message = "[游戏分类ID]字段不能为空")
	private Integer categoryId;
	//商品名称
	@NotNull(message = "[商品名称]字段不能为空")
	private String productName;
	//手机号
	@NotNull(message = "[手机号不能为空]字段不能为空")
	private String mobile;
	//角色名
	private String rolename;

	@NotNull(message = "[游戏区服]字段不能为空")
	private String gameArea;
	//价格
	@NotNull(message = "[价格为空]字段不能为空")
	private BigDecimal price;
	//数量
	private Integer amount;
	//创建时间
	private Date createTime;
	//
	private Date updateTime;

}
