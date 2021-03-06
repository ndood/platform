package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


/**
 * 动态文件表
 * 
 * @author shijiaoyun
 * @date 2018-08-30 11:02:13
 */
@Data
public class DynamicFile implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//动态id
	private Integer dynamicId;
	//文件链接地址
	private String url;
	//文件类型(1：图片；2：视频，由于同一条动态不能同时选图片和视频，因此将此属性放到主表中)
	private Integer type;
	//播放次数（视频才会有）
	private Integer playCount;
	//创建时间（预留）
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	//修改时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;
	//状态（1：有效；0：无效）
	private Integer status;
	// 图片、视频宽度
	private Integer width;
	// 图片、视频高度
	private Integer height;
	// 视频时长（单位秒）
	private Integer duration;

}
