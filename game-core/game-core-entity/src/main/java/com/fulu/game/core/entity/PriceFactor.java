package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 价格系数表
 * 
 * @author wangbin
 * @date 2018-06-26 14:30:55
 */
@Data
public class PriceFactor implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//价格系数
	private BigDecimal factor;
	//
	private String categoryIds;
	//
	private Integer adminId;
	//
	private String adminName;
	//
	private Date createTime;

}
