package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 技能等级表
 * 
 * @author shijiaoyun
 * @date 2018-09-25 11:47:10
 */
@Data
public class TechLevel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//技能等级名称
	private String name;
	//排序号
	private Integer sort;
	//操作员id
	private Integer operatorId;
	//操作员名称
	private String operatorName;
	//创建时间
	private Date createTime;
	//修改时间
	private Date updateTime;
	//删除标志（1：删除；0：未删除）
	private Integer isDel;

}
