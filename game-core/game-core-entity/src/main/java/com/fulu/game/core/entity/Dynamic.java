package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


/**
 * 动态表
 * 
 * @author shijiaoyun
 * @date 2018-08-30 10:31:41
 */
@Data
public class Dynamic implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//动态id
	private Long id;
	//动态发布用户id
	private Integer userId;
	//商品id
	private Integer productId;
	//动态内容
	private String content;
	//动态类型(0：文字；1：图片；2：视频)
	private Integer type;
	//城市编码（用于查询附近的动态）
	private String cityCode;
	//城市名称
	private String cityName;
	//地理位置hash（用于查询附近的动态）
	private String geohash;
	//地理位置hash（用于查询附近的动态，去掉geohash后两位）
	private String geohashShort;
	//经度（用于计算距离）
	private Double lon;
	//纬度（用于计算距离）
	private Double lat;
	//是否置顶（1：是；0：否）
	private Integer isTop;
	//是否热门（1：是；0：否，预留）
	private Integer isHot;
	//打赏次数
	private Long rewards;
	//点赞次数
	private Long likes;
	//评论次数
	private Long comments;
	//举报次数（预留）
	private Long reports;
	//点击次数（预留）
	private Long clicks;
	//创建时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	//修改时间（预留，暂不确定是否有修改功能）
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;
	//动态状态（1：有效；0：无效）
	private Integer status;

	/** 下单数 */
	private Integer orderCount;
	/** 后端操作者id */
	private Integer operatorId;
	/** 后端操作者名称 */
	private String operatorName;

}
