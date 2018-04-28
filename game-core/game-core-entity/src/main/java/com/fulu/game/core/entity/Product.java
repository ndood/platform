package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 商品表
 * 
 * @author yanbiao
 * @date 2018-04-27 16:27:58
 */
@Data
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//用户商品
	private Integer userId;

	private String categoryIcon;
	//游戏ID
	private Integer categoryId;
	//游戏名称
	private String productName;
	//关联技能
	private Integer techAuthId;
	//价格
	private BigDecimal price;
	//单位类型
	private String unit;
	//关联的销售单位tech_value_id
	private Integer unitTechValueId;
	//销售单位权重
	private Integer unitTechValueRank;
	//状态
	private Boolean status;
	//创建时间
	private Date createTime;
	//修改时间
	private Date updateTime;

}
