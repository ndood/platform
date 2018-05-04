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

	private Integer gender;

	private String categoryIcon;
	//游戏ID
	private Integer categoryId;
	//游戏名称
	private String productName;
	//技能描述
	private String description;
	//关联技能
	private Integer techAuthId;
	//价格
	private BigDecimal price;
	//单位类型
	private String unit;
	//关联的销售单位tech_value_id
	private Integer salesModeId;
	//销售单位权重
	private Integer salesModeRank;
	//状态
	private Boolean status;
	//创建时间
	private Date createTime;
	//修改时间
	private Date updateTime;

}
