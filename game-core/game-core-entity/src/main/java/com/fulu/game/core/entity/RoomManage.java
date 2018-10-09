package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 房间管理表
 * 
 * @author wangbin
 * @date 2018-10-09 15:19:25
 */
@Data
public class RoomManage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//房间ID
	private String roomNo;
	//用户ID
	private Integer userId;
	//角色类型(1房主,2管理,3主持)
	private Integer role;
	//创建时间
	private Date createTime;
	//
	private Date updateTime;

}
