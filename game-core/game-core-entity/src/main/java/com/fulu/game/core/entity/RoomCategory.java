package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 房间分类表
 * 
 * @author wangbin
 * @date 2018-10-07 01:06:59
 */
@Data
public class RoomCategory implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//父类ID
	private Integer pid;
	//分类名称
	private String name;
	// 房间图标
	private String icon;
	//是否激活(1是,0否)
	private Boolean isActivate;
	//创建时间
	private Date createTime;
	//
	private Date updateTime;

}