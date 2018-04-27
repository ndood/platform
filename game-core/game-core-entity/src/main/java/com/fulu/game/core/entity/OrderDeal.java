package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 
 * 
 * @author yanbiao
 * @date 2018-04-26 17:51:54
 */
@Data
public class OrderDeal implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//订单号
	private String orderNo;
	//订单处理类型
	private Integer type;
	//处理过程备注
	private String remark;
	//
	private Date createTime;

}
