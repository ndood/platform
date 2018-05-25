package com.fulu.game.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 好友认可记录表
 *
 * @author yanbiao
 * @date 2018-05-25 12:15:34
 */
@Data
public class Approve implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    private Integer id;
    //技能拥有者id
    private Integer techOwnerId;
    //认可的技能
    private Integer techAuthId;
    //点击认可的用户的id
    private Integer userId;
    //生成时间
    private Date createTime;

}
