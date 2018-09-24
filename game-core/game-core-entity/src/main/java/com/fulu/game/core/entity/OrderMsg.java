package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 
 * 
 * @author wangbin
 * @date 2018-09-24 22:35:17
 */
@Data
public class OrderMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//消息内容
	private String message;
	//订单号
	private String orderNo;
	//消息人ID
	private Integer userId;
	//对方昵称
	private String oppUsername;
	//对方头像
	private String oppHeadUrl;
	//订单名称
	private String orderName;
	//订单服务时间
	private Date orderTime;
	//订单状态
	private Integer orderStatus;
	//创建时间
	private Date createTime;
	//
	private Date updateTime;

}
