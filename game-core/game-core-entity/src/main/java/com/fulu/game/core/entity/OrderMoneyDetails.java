package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 订单流水表
 * 
 * @author yanbiao
 * @date 2018-04-26 14:07:31
 */
@Data
public class OrderMoneyDetails implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//订单号
	private String orderNo;
	//流水用户
	private Integer userId;
	//描述
	private String remark;
	//流水金额
	private String money;
	//
	private Date createTime;

}
