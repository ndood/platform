package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 领航订单流水表
 * 
 * @author Gong Zechun
 * @date 2018-07-05 14:31:31
 */
@Data
public class PilotOrderDetails implements Serializable {
	private static final long serialVersionUID = -7420785925080374339L;

	//
	private Integer id;
	//
	private String remark;
	//本次金额
	private BigDecimal money;
	//剩余金额
	private BigDecimal leftAmount;
	//总金额
	private BigDecimal sum;
	//管理员ID
	private Integer adminId;
	//管理员名称
	private String adminName;
	//
	private Date createTime;

}
