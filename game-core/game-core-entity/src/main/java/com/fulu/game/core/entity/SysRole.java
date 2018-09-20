package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


/**
 * 角色表
 * 
 * @author shijiaoyun
 * @date 2018-09-19 16:25:01
 */
@Data
public class SysRole implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//名称
	private String name;
	//备注信息
	private String remark;
	//是否启用（1：是；0：否）
	private Integer status;
	//操作员id
	private Integer operatorId;
	//操作员名称
	private String operatorName;
	//创建时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;
	//修改时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date updateTime;
	//删除标识（1：删除；0：未删除）
	private Integer isDel;

}
