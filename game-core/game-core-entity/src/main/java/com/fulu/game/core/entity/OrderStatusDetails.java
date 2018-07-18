package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 
 * 
 * @author wangbin
 * @date 2018-07-18 12:04:41
 */
@Data
public class OrderStatusDetails implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//订单号
	private String orderNo;
	//订单状态
	private Integer orderStatus;
	//订单触发时间
	private Date triggerTime;
	//倒计时时间
	private Integer countDownMinute;
	//是否有效
	private Boolean isValid;
	//创建时间
	private Date createTime;
	//
	private Date updateTime;

}
