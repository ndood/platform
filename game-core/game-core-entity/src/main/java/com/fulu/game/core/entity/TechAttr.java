package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 技能属性表
 * 
 * @author wangbin
 * @date 2018-04-18 16:29:27
 */
@Data
public class TechAttr implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//
	private Integer categoryId;
	//游戏类型
	private Integer type;
	//游戏ID
	private String name;
	//技能字典名称(段位)
	private Boolean status;
	//
	private Date createTime;
	//
	private Date updateTime;

}
