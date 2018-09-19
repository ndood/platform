package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 角色路由表
 * 
 * @author shijiaoyun
 * @date 2018-09-19 16:32:36
 */
@Data
public class RoleRouter implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//角色id
	private Integer roleId;
	//路由id
	private Integer routerId;

}
