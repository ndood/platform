package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 用户技能具体信息表
 * 
 * @author wangbin
 * @date 2018-04-23 11:17:40
 */
@Data
public class UserTechInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//技能认证ID
	private Integer techAuthId;
	//技能属性ID
	private Integer techAttrId;
	//技能属性名称
	private String attr;
	//技能属性值ID
	private Integer techValueId;
	//技能属性值
	private String value;
	//
	private Boolean status;
	//排名
	private Integer rank;
	//
	private Date createTime;
	//
	private Date updateTime;

}
