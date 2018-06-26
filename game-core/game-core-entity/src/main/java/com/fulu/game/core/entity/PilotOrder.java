package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 
 * 
 * @author wangbin
 * @date 2018-06-26 14:44:22
 */
@Data
public class PilotOrder implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//
	private String orderNo;
	//
	private String name;
	//
	private String couponNo;
	//
	private Integer categoryId;
	//
	private Integer userId;
	//
	private Integer serviceUserId;
	//
	private String remark;
	//商品数量
	private Integer productNum;
	//商品原始价格
	private BigDecimal productPrice;
	//领航商品价格
	private BigDecimal pilotProductPrice;
	//平台订单总额
	private BigDecimal totalMoney;
	//领航订单总额
	private BigDecimal pilotTotalMoney;
	//
	private BigDecimal spreadMoney;
	//该订单是否完成
	private Boolean isComplete;
	//
	private Date createTime;

}
