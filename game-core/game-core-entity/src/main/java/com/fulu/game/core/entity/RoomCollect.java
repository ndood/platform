package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 房间收藏
 * 
 * @author wangbin
 * @date 2018-10-09 13:17:47
 */
@Data
public class RoomCollect implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//
	private String roomNo;
	//
	private Integer userId;
	//
	private Date createTime;
	//
	private Date updateTime;

}
