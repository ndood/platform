package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 聊天室派单表
 * 
 * @author wangbin
 * @date 2018-10-14 21:02:12
 */
@Data
public class RoomAssignOrder implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//房间号码
	private String roomNo;
	//派单内容
	private String content;
	//派单游戏
	private Integer categoryId;
	//那个房间管理发的派单
	private Integer roomManagerUserId;
	//派单状态
	private Boolean status;
	//
	private Date createTime;
	//
	private Date updateTime;

}
