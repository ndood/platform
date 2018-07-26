package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 
 * 
 * @author wangbin
 * @date 2018-07-26 19:42:13
 */
@Data
public class UserAutoReceiveOrder implements Serializable {
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
	//自动派单数
	private Integer orderNum;
	//自动派单完成数
	private Integer orderCompleteNum;
	//派单取消数
	private Integer orderCancelNum;
	//派单协商仲裁数
	private Integer orderDisputeNum;
	//派单失败率
	private Double orderFailPercent;
	//用户自己自动接单设置
	private Boolean userAutoSetting;
	//
	private Integer adminId;
	//
	private String adminName;
	//
	private Date createTime;
	//
	private Date updateTime;

}
