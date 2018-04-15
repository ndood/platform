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
public class SysUser implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//
	private String name;
	//
	private String username;
	//
	private String password;
	//
	private String salt;
	//状态(0：未激活，1：激活)
	private Boolean state;
	//
	private Date createTime;
	//
	private Date updateTime;

}
