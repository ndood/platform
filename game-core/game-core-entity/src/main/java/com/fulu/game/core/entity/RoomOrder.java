package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 聊天室产生的订单表
 * 
 * @author wangbin
 * @date 2018-10-14 15:22:05
 */
@Data
public class RoomOrder implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//1:订单,2:礼物
	private Integer type;
	//房间号
	private String roomNo;
	//订单号
	private String orderNo;
	//订单创建时间
	private Date createTime;

}
