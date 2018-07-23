package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 段位定级表
 * 
 * @author wangbin
 * @date 2018-07-23 19:34:58
 */
@Data
public class GradingPrice implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//游戏分类
	private Integer categoryId;
	//父类属性id
	private Integer pid;
	//单位名称
	private String name;
	//权重
	private Integer rank;
	//
	private BigDecimal price;
	//创建时间
	private Date createTime;
	//
	private Date updateTime;

}
