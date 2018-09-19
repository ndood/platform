package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;


/**
 * 路由表
 * 
 * @author shijiaoyun
 * @date 2018-09-19 16:21:35
 */
@Data
public class SysRouter implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//Id
	private Integer id;
	//路由id
	private Integer pid;
	//路由类型（1：菜单；2：权限；）
	private Integer type;
	//地址
	private String path;
	//组件名
	private String component;
	//名称
	private String name;
	//对应图标地址
	private String iconcls;
	//是否显示（1：是；0：否）
	private Integer hidden;
	//操作员id
	private Integer operatorId;
	//操作员名称
	private String operatorName;
	//创建时间
	private Date createTime;
	//修改时间
	private Date updateTime;
	//删除标识（1：删除；0：未删除）
	private Integer isDel;

	private List<SysRouter> child;

}
