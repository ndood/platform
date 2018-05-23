package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


/**
 * 优惠券发放用户
 * 
 * @author wangbin
 * @date 2018-05-15 18:34:37
 */
@Data
public class CouponGrantUser implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//优惠券编码
	@Excel(name = "优惠券编码",orderNum = "0",width = 15)
	private String couponNo;
	//发放记录ID
	private Integer couponGrantId;
	//发放用户ID
	private Integer userId;
	//发放手机号
	@Excel(name = "发放手机号",orderNum = "1",width = 15)
	private String mobile;
	//是否发放成功
	@Excel(name = "发放成功",replace = {"是_true","否_false"},orderNum = "2")
	private Boolean isSuccess;
	//发放错误原因
	@Excel(name = "发放错误原因",orderNum = "3",width = 20)
	private String errorCause;
	//发放时间
	@Excel(name = "发放时间",format = "yyyy-MM-dd HH:mm:ss",orderNum = "4",width = 20)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;

}
