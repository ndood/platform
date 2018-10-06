package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 房间管理表
 * 
 * @author wangbin
 * @date 2018-10-07 00:25:52
 */
@Data
public class RoomManage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//房间ID
	private Integer roomNo;
	//用户ID
	private Integer userId;
	//
	private String headUrl;
	//用户昵称
	private String nickname;
	//性别(1男,2女)
	private Integer gender;
	//角色类型(1房主,2管理,3主持)
	private Integer role;
	//创建时间
	private Date createTime;
	//
	private Date updateTime;

}
