package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 订单表
 * 
 * @author yanbiao
 * @date 2018-04-25 18:27:54
 */
@Data
public class Order implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//订单号
	private String orderNo;
	//下单用户ID
	private Integer userId;
	//陪玩师用户ID
	private Integer serviceUserId;
	//订单游戏分类
	private Integer categoryId;

	//订单名称
	private String name;
	//备注
	private String remark;
	//订单状态
	private Integer status;
	//是否支付(1:已支付,2:未支付)
	private Boolean isPay;
	//佣金
	private BigDecimal commissionMoney;
	//订单总额
	private BigDecimal totalMoney;
	//订单创建时间
	private Date createTime;
	//
	private Date updateTime;
	//订单支付时间
	private Date payTime;
	//订单完成时间
	private Date completeTime;

}
