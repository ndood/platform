package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 分期乐订单拓展表
 * 
 * @author Gong Zechun
 * @date 2018-08-14 18:19:17
 */
@Data
public class FenqileOrder implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//订单编号
	private String orderNo;
	//支付编号
	private String paymentNo;
	//备注
	private String remark;
	//修改时间
	private Date updateTime;
	//创建时间
	private Date createTime;

}
