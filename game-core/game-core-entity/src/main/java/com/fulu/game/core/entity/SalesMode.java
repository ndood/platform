package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * @author wangbin
 * @date 2018-05-04 18:48:12
 */
@Data
public class SalesMode implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//类型ID
	private Integer categoryId;
	//种类
	private Integer type;
	//单位
	private String name;
	//价格
	private BigDecimal price;
	//权重
	private Integer rank;
	//
	private Date createTime;
	//
	private Date updateTime;

}
