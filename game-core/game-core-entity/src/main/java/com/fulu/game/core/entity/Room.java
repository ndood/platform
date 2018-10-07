package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;


/**
 * 聊天室
 * 
 * @author wangbin
 * @date 2018-10-07 10:26:35
 */
@Data
public class Room implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//房间号码
	private String roomNo;
	//图标
	private String icon;
	//房间名称
	private String name;
	//房间标语
	private String slogan;
	//房间公告
	private String notice;
	//是否上锁
	private Integer isLock;
	//房间密码
	@JsonIgnore
	private String password;
	//用户ID
	private Integer userId;
	//所有者手机号
	private String ownerMobile;
	//虚拟人数
	private Integer virtualPeople;
	//是否显示
	private Boolean isShow;
	//是否激活
	private Boolean isActivate;
	//是否是热门推荐
	private Boolean isHot;
	//排序号
	private Integer sort;
	//房间模板(1派单房，2娱乐房)
	private Integer template;
	//游戏分类
	private Integer categoryId;
	//房间分类
	private Integer roomCategoryId;
	//备注
	private String remark;
	//
	private Date createTime;
	//
	private Date updateTime;

}
