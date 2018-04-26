package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


/**
 * 零钱流水表
 * 
 * @author yanbiao
 * @date 2018-04-25 14:59:54
 */
@Data
public class MoneyDetails implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键id自增
	private Integer id;
	//操作者id(用户或管理员)
	private Integer operatorId;
	//对象id(对谁加宽或者提款)
	private Integer targetId;
	//金额(默认0.00)
	private BigDecimal money;
	//1表示入款，-1表示提款
	private Integer action;
	//action为-1时关联t_cash_draws表
	private Integer cashId;
	//action后的金额与用户的金额同步
	private BigDecimal sum;
	//备注
	private String remark;
	//记录生成时间
	private Date createTime;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	public Date getCreateTime(){
		return createTime;
	}

}
