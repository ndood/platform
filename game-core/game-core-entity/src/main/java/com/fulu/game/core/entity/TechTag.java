package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 技能标签关联表
 * 
 * @author wangbin
 * @date 2018-04-23 13:56:51
 */
@Data
public class TechTag implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//技能ID
	private Integer techAuthId;
	//标签ID
	private Integer tagId;
	//
	private String name;
	//
	private Date createTime;
	//
	private Date updateTime;

}
