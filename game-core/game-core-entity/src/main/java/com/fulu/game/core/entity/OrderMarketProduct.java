package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
	@JsonIgnore
	private String orderNo;

	private String type;
	//游戏分类ID
	private Integer categoryId;
	//商品名称
	private String productName;
	//手机号
	private String mobile;
	//角色名
	private String rolename;

	private String gameArea;
	//价格
	@JsonIgnore
	private BigDecimal price;
	//数量
	@JsonIgnore
	private Integer amount;
	//创建时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	//
	private Date updateTime;

}
