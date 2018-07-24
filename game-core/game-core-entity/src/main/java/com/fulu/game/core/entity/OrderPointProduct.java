package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 上分订单详情
 * 
 * @author wangbin
 * @date 2018-07-24 17:55:45
 */
@Data
public class OrderPointProduct implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//
	private String orderNo;
	//上分类学
	private Integer pointType;
	//
	private Integer categoryId;
	//
	private Integer areaId;
	//
	private Integer gradingPriceId;
	//
	private Integer targetGradingPriceId;
	//
	private String categoryName;
	//
	private String accountInfo;
	//
	private String orderChoice;
	//
	private BigDecimal price;
	//
	private Integer amount;
	//
	private Date createTime;
	//
	private Date updateTime;

}
