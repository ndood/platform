package com.fulu.game.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 举报表
 *
 * @author Gong Zechun
 * @date 2018-08-27 13:55:55
 */
@Data
public class Report implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    private Integer id;
    //举报人id
    private Integer userId;
    //被举报人id
    private Integer reportedUserId;
    //举报内容
    private String content;
    //处理状态(0：未处理（默认），1：已处理)
    private Integer status;
    //处理时间
    private Date processTime;
    //备注
    private String remark;
    //管理员id
    private Integer adminId;
    //管理员用户名
    private String adminName;
    //修改时间
    private Date updateTime;
    //创建时间
    private Date createTime;

}
