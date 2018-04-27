package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author yanbiao
 * @date 2018-04-24 16:45:40
 */
@Data
public class CashDraws implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//提现记录id
	private Integer cashId;
	//提现申请时间
	private Date createTime;
	//申请人
	private Integer userId;
	//申请者昵称
	private String nickname;
	//申请者手机号
	private String mobile;
	//提现金额（默认0.00）
	private BigDecimal money;
	//账户账号
	private String accNo;
	//账户名
	private String accUser;
	//备注（由运营人员填写）
	private String comment;
	//申请单处理状态（生成订单时默认为0未处理，1表示已处理）
	private Integer cashStatus;
	//申请单操作人
	private String operator;
	//提现单号（处理成功后生成）
	private String cashNo;
	//处理时间
	private Date processTime;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	public Date getCreateTime(){
		return createTime;
	}
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	public Date getProcessTime(){
		return processTime;
	}
}