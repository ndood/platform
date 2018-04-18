package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 系统管理员表
 * 
 * @author wangbin
 * @date 2018-04-18 15:38:16
 */
@Data
public class Member implements Serializable {
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
	//
	private Integer status;
	//
	private Date createTime;
	//
	private Date updateTime;

}
