package com.fulu.game.core.entity;

import lombok.Data;

import java.io.Serializable;



/**
 * 
 * 
 * @author wangbin
 * @date 2018-04-15 19:14:30
 */
@Data
public class SysRole implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//角色标识
	private String role;
	//描述
	private String description;
	//是否可用
	private Boolean available;

}
