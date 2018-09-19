package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 用户角色表
 * 
 * @author shijiaoyun
 * @date 2018-09-19 16:28:46
 */
@Data
public class AdminRole implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//用户id
	private Integer adminId;
	//角色id
	private Integer roleId;

}
