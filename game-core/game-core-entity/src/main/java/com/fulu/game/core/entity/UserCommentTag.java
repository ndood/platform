package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 
 * 
 * @author wangbin
 * @date 2018-09-15 18:44:02
 */
@Data
public class UserCommentTag implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//评论ID
	private Integer commentId;
	//技能ID
	private Integer techAuthId;
	//标签ID
	private Integer tagId;
	//
	private String tagName;
	//
	private Date createTime;

}
