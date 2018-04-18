package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 标签表
 * 
 * @author wangbin
 * @date 2018-04-18 16:29:26
 */
@Data
public class Tag implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//父类id（根目录0）
	private Integer pid;
	//标签名称
	private String name;
	//性别(0:不限制,1:男，2:女)
	private Integer gender;
	//创建时间
	private Date createTime;
	//
	private Date updateTime;

}
