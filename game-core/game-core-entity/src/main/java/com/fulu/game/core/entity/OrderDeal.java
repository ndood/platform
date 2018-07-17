package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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

	private Integer userId;

	private String title;

	//订单号
	private String orderNo;

	private Integer orderStatus;
	//退款金额
	private BigDecimal refundMoney;
	//是否有效(取消协商)
	private Boolean isValid;
	//订单处理类型
	private Integer type;
	//处理过程备注
	private String remark;
	//
	private Date createTime;

}
