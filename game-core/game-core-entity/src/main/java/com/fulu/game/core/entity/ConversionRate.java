package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 转换率统计表
 * 
 * @author shijiaoyun
 * @date 2018-09-25 11:52:10
 */
@Data
public class ConversionRate implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//统计显示名称
	private String name;
	//统计时间段
	private String timeBucket;
	//下单人数
	private Integer peoples;
	//下单数
	private Integer orders;
	//下单总金额
	private BigDecimal amount;
	//新人下单人数
	private Integer newPeoples;
	//新人下单数
	private Integer newOrders;
	//新人下单总金额
	private BigDecimal newAmount;
	//新人付款人数
	private Integer newPays;
	//新人下单转化率
	private BigDecimal newOrderRate;
	//新人付款转化率
	private BigDecimal newPayRate;
	//复购下单人数
	private Integer repeatOrders;
	//复购付款人数
	private Integer repeatPays;
	//复购下单转化率
	private BigDecimal repeatOrderRate;
	//复购付款转化率
	private BigDecimal repeatPayRate;
	//统计时间
	private Date createTime;
	//修改时间
	private Date updateTime;

}
