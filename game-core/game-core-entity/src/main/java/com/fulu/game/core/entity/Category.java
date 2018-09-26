package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fulu.game.core.entity.vo.ProductShowCaseVO;
import com.github.pagehelper.PageInfo;
import lombok.Data;


/**
 * 分类表
 * 
 * @author wangbin
 * @date 2018-04-18 16:24:51
 */
@Data
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//父ID
	private Integer pid;
	//图标
	private String icon;
	//首页图标
	private String indexIcon;
	//游戏名称
	private String name;
	//状态(1激活,0失效)
	private Boolean status;
	//排序
	private Integer sort;
	//手续费
	private BigDecimal charges;

	private Boolean isPoint;

	// 示例图片
	private String examplePicUrl;
	// 示例说明
	private String exampleAbout;
	//
	private Date createTime;
	//
	private Date updateTime;

	//分类下的商品分页列表信息
	private List<ProductShowCaseVO> productList;

}
