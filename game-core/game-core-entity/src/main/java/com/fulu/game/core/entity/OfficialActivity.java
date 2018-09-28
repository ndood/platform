package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 官方公告
 * 
 * @author wangbin
 * @date 2018-09-28 18:09:57
 */
@Data
public class OfficialActivity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//平台
	private Integer platform;
	//活动类型(1:优惠券，2:链接)
	private Integer type;
	//活动文案
	private String content;
	//路由地址
	private String route;
	//激活(1激活,0不可用)
	private Boolean isActivate;
	//开始时间
	private Date beginTime;
	//结束时间
	private Date endTime;
	//备注
	private String remark;
	//
	private Date createTime;
	//
	private Date updateTime;
	//是否逻辑删除(1是,0否)
	private Boolean isDel;

}
