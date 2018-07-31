package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.CharMatcher;
import lombok.Data;
import lombok.ToString;


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

	//是否激活
	private Boolean isActivate;

	//删除标记
	private Boolean delFlag;


	@Override
	public String toString() {
		String toStr="Product{" +
				"id=" + id +
				", userId=" + userId +
				", gender=" + gender +
				", categoryIcon='" + categoryIcon + '\'' +
				", categoryId=" + categoryId +
				", productName='" + productName + '\'' +
				", techAuthId=" + techAuthId +
				", price=" + price +
				", unit='" + unit + '\'' +
				", salesModeId=" + salesModeId +
				", salesModeRank=" + salesModeRank +
				", status=" + status +
				", createTime=" + createTime +
				", updateTime=" + updateTime +
				'}';
		Pattern pattern = Pattern.compile("(\r\n|\r|\n|\n\r)");
		Matcher m = pattern.matcher(toStr);
		if (m.find()) {
			toStr = m.replaceAll("");
		}
		return toStr;
	}
}
