package com.fulu.game.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/8/27 10:56.
 * @Description: 用户好友
 */
@Data
public class UserFriend implements Serializable {

    /** 主键id，自增 */
    private Integer id;
    /** 发起操作用户id */
    private Integer fromUserId;
    /** 目标用户id */
    private Integer toUserId;
    /** 创建时间 */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
    /** 修改时间 */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;
    /** 是否关注（1：是；0：否）*/
    private Integer isAttention;
    /** 是否黑名单（1：是；0：否）*/
    private Integer isBlack;
    /** 状态（1：有效；0：无效） */
    private Integer status;

}
