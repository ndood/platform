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
public class SysUserRole implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer roleId;
	//
	private Integer userId;

}
