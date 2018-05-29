package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 技能认证信息驳回表
 * 
 * @author wangbin
 * @date 2018-05-28 20:04:35
 */
@Data
public class UserTechAuthReject implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//
	private Integer userId;
	//技能认证ID
	private Integer userTechAuthId;
	//技能认证状态
	private Integer userTechAuthStatus;
	//原因
	private String reason;
	//管理员ID
	private Integer adminId;
	//管理员名称
	private String adminName;
	//创建时间
	private Date createTime;

}
