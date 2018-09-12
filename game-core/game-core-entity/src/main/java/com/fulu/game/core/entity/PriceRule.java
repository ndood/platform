package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 定价规则表
 * 
 * @author shijiaoyun
 * @date 2018-09-12 15:43:00
 */
@Data
public class PriceRule implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//类型ID
	private Integer categoryId;
	//显示平台(1小程序，2APP，3都显示)
	private Integer platformShow;
	//接单数
	private Integer orderCount;
	//价格，同时用于排序
	private BigDecimal price;
	//
	private Date createTime;
	//
	private Date updateTime;
	//删除标记（1：删除；0：正常）
	private Integer isDel;

}
