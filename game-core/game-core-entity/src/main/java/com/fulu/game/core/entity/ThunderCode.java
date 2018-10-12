package com.fulu.game.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 迅雷活动兑换码
 *
 * @author Gong Zechun
 * @date 2018-10-12 09:50:54
 */
@Data
public class ThunderCode implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    private Integer id;
    //用户id
    private Integer userId;
    //是否被使用(0:否,1:是)
    private Boolean isUse;
    //会员兑换码
    private String code;
    //账号
    private String account;
    //密码
    private String password;
    //类型（1：搜狐；2：喜马拉雅；3：steam游戏CDK；4：steam账号和密码；5：CIBN会员）
    private Integer type;
    //更新时间
    private Date updateTime;
    //创建时间
    private Date createTime;
    //删除标记（0：未删除，1：已删除）
    private Boolean delFlag;

}
