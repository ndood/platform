package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 价格系数表
 * 
 * @author wangbin
 * @date 2018-06-25 18:07:42
 */
@Data
public class PriceFactor implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//价格系数
	private BigDecimal factor;
	//
	private Integer adminId;
	//
	private String adminName;
	//
	private Date createTime;

}
