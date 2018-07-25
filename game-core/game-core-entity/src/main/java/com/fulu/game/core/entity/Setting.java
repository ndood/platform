package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 
 * 
 * @author wangbin
 * @date 2018-07-25 18:48:32
 */
@Data
public class Setting implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//设置类型
	private Integer type;
	//系统值
	private String val;
	//
	private Integer adminId;
	//
	private String adminName;
	//
	private Date createTime;

}
