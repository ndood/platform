package com.fulu.game.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;



/**
 * 
 * 
 * @author wangbin
 * @date 2018-04-15 19:14:30
 */
@Data
public class SysPermission implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//名称
	private String name;
	//资源类型
	private String resourceType;
	//资源路径
	private String url;
	//权限字符串
	private String permission;
	//夫编号
	private Integer parentId;
	//
	private String parentIds;
	//
	private String available;

}
