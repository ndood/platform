package com.fulu.game.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 用户微信群分享表
 *
 * @author Gong Zechun
 * @date 2018-07-25 10:41:56
 */
@Data
public class UserWechatGroupShare implements Serializable {
    private static final long serialVersionUID = 1L;

    //
    private Integer id;
    //用户ID
    private Integer userId;
    //用户是否已完成分享任务（0：否，1：是）
    private Integer shareStatus;
    //分享到的微信群数量
    private Integer groupCounts;
    //分享到的微信群的群号（多个以逗号隔开）
    private String groupIds;
    //备注
    private String remark;
    //更新时间
    private Date updateTime;
    //创建时间
    private Date createTime;

}
