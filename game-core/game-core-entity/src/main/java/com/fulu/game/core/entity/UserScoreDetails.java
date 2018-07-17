package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 用户积分详情表
 * 
 * @author Gong Zechun
 * @date 2018-07-17 14:15:01
 */
@Data
public class UserScoreDetails implements Serializable {
	private static final long serialVersionUID = -1268057835316153472L;

	//
	private Integer id;
	//用户id
	private Integer userId;
	//积分变动
	private Integer score;
	//积分变动描述
	private String description;
	//备注
	private String remark;
	//记录生成时间
	private Date createTime;

}
