package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 系统管理员表
 * 
 * @author yanbiao
 * @date 2018-04-24 10:20:44
 */
@Data
public class Admin implements Serializable {
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
	//状态(0失效,1启用)
	private Integer status;
	//
	private Date createTime;
	//
	private Date updateTime;

}
