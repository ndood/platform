package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 系统单位表
 * 
 * @author wangbin
 * @date 2018-07-31 11:18:12
 */
@Data
public class SysUnit implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//单位名称
	private String unit;
	//创建时间
	private Date createTime;

}
