package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 商品表
 * 
 * @author yanbiao
 * @date 2018-04-24 15:30:26
 */
@Data
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//用户商品
	private Integer userId;
	//游戏ID
	private Integer categoryId;
	//游戏名称
	private String categoryName;
	//关联技能
	private Integer techAuthId;
	//价格
	private BigDecimal price;
	//单位ID
	private Integer unitTechValueId;
	//单位类型
	private String unit;
	//状态
	private Boolean status;
	//创建时间
	private Date createTime;
	//修改时间
	private Date updateTime;

}
