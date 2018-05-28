package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 用户认证信息驳回表
 * 
 * @author wangbin
 * @date 2018-05-28 12:12:52
 */
@Data
public class UserInfoAuthReject implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//用户ID
	private Integer userId;
	//用户信息认证ID
	private Integer userInfoAuthId;
	//
	private Integer userInfoAuthStatus;
	//原因
	private String reason;
	//创建时间
	private Date createTime;

}
