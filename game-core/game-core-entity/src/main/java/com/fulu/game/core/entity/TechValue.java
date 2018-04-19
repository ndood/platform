package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 技能属性值表
 * 
 * @author wangbin
 * @date 2018-04-18 16:39:55
 */
@Data
public class TechValue implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//
	private Integer techAttrId;
	//
	private String name;
	//
	private Boolean status;
	//
	private Integer rank;
	//
	private Date createTime;
	//
	private Date updateTime;

}
