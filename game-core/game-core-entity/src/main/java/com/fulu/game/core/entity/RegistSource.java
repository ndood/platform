package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


/**
 * 
 * 
 * @author yanbiao
 * @date 2018-06-13 15:33:21
 */
@Data
public class RegistSource implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键id
	private Integer id;
	//注册来源名
	private String name;
	//操作人id
	private Integer adminId;
	//备注
	private String remark;
	//小程序码url
	private String wxcodeUrl;
	//生成时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	//修改时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;

}
