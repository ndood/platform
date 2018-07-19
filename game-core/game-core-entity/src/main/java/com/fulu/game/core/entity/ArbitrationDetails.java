package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 仲裁结果流水表
 * 
 * @author Gong Zechun
 * @date 2018-07-19 12:23:30
 */
@Data
public class ArbitrationDetails implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//订单号
	private String orderNo;
	//用户ID
	private Integer userId;
	//陪玩师ID
	private Integer serviceUserId;
	//退款给用户的金额
	private BigDecimal refundUserMoney;
	//退款给陪玩师的金额
	private BigDecimal refundServiceUserMoney;
	//平台收入金额
	private BigDecimal commissionMoney;
	//备注
	private String remark;
	//修改时间
	private Date updateTime;
	//创建时间
	private Date createTime;

}
