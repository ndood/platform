package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 推送信息表
 * 
 * @author wangbin
 * @date 2018-06-06 17:10:37
 */
@Data
public class PushMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//推送类型
	private Integer type;
	//落地页
	private String page;
	//推送ID
	private String pushIds;
	//触发时间
	private Date touchTime;
	//推送内容
	private String content;
	//点击数
	private Long hits;
	//成功数
	private Integer successNum;
	//推送总数
	private Integer totalNum;
	//是否推送过了
	private Boolean isPushed;
	//管理员ID
	private Integer adminId;
	//管理员名称
	private String adminName;
	//创建时间
	private Date createTime;
	//
	private Date updateTime;

}
