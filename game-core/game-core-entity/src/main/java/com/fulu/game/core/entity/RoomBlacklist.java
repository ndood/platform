package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 房间黑名单
 * 
 * @author wangbin
 * @date 2018-10-11 17:21:16
 */
@Data
public class RoomBlacklist implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//房间
	private String roomNo;
	//用户ID
	private Integer userId;
	//
	private Date createTime;

}
