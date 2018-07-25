package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 
 * 
 * @author wangbin
 * @date 2018-07-25 19:22:19
 */
@Data
public class AutoReceivingOrder implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//用户ID
	private Integer userId;
	//游戏技能ID
	private Integer techAuthId;
	//游戏ID
	private Integer categoryId;
	//选择大区ID（为空则为全部）
	private Integer areaId;
	//接单范围开始
	private Integer startRank;
	//接单范围结束
	private Integer endRank;
	//备注
	private String remark;
	//用户自己自动接单设置
	private Boolean userAutoSetting;
	//
	private Integer adminId;
	//
	private String adminName;
	//
	private Date createTime;

	private Date updateTime;

}
