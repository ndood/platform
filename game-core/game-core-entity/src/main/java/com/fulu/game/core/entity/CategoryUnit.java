package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 游戏单位
 * 
 * @author wangbin
 * @date 2018-07-31 11:18:12
 */
@Data
public class CategoryUnit implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//游戏分类
	private Integer categoryId;
	//单位ID
	private Integer unitId;
	//单位名称
	private String unit;
	//
	private Date createDate;

}
