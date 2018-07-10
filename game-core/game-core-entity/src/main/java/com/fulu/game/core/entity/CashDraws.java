package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
	//申请人
	@Excel(name = "申请人", orderNum = "1", width = 15)
	private Integer userId;
	//申请者昵称
	@Excel(name = "申请者昵称", orderNum = "2", width = 15)
	private String nickname;
	//申请者手机号
	@Excel(name = "申请者手机号", orderNum = "3", width = 15)
	private String mobile;
	//提现金额（默认0.00）
	@Excel(name = "提现金额", orderNum = "4", width = 15)
	private BigDecimal money;
	//账户账号
	@Excel(name = "账户账号", orderNum = "5", width = 15)
	private String accNo;
	//账户名
	@Excel(name = "账户名", orderNum = "6", width = 15)
	private String accUser;
	//备注（由运营人员填写）
	@Excel(name = "备注", orderNum = "7", width = 50)
	private String comment;
	//申请单处理状态（生成订单时默认为0未处理，1表示已处理）
	@Excel(name = "申请单处理状态", orderNum = "8", width = 15)
	private Integer cashStatus;
	//申请单操作人
	@Excel(name = "申请单操作人", orderNum = "9", width = 15)
	private String operator;
	//提现单号（处理成功后生成）
	@Excel(name = "提现单号", orderNum = "10", width = 15)
	private String cashNo;
	//提现申请时间
	@Excel(name = "提现申请时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "11", width = 25)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;
	//处理时间
	@Excel(name = "处理时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "12", width = 25)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date processTime;

}
