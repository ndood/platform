package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 段位定级价格表
 * 
 * @author wangbin
 * @date 2018-07-24 11:54:28
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
	//价格
	private BigDecimal price;
	//最后修改管理员
	private Integer adminId;
	//
	private String adminName;
	//创建时间
	private Date createTime;
	//
	private Date updateTime;

}
