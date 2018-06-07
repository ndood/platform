package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 商品置顶表
 * 
 * @author wangbin
 * @date 2018-06-07 15:28:32
 */
@Data
public class ProductTop implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//指定分类
	private Integer categoryId;
	//用户ID
	private Integer userId;
	//手机号
	private String mobile;
	//上下架(0下架,1上架)
	private Boolean status;
	//排序
	private Integer sort;
	//
	private Integer adminId;
	//
	private String adminName;
	//
	private Date createTime;
	//
	private Date updateTime;

}
