package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;

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
	//发放记录ID
	private Integer couponGrantId;
	//发放用户ID
	private Integer userId;
	//发放手机号
	private String mobile;
	//是否发放成功
	private Boolean isSuccess;
	//发放错误原因
	private String errorCause;
	//发放时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;

}
