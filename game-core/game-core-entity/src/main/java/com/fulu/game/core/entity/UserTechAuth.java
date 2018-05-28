package com.fulu.game.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 技能认证表
 *
 * @author wangbin
 * @date 2018-04-23 11:17:40
 */
@Data
public class UserTechAuth implements Serializable {
    private static final long serialVersionUID = 1L;

    //
    private Integer id;
    //游戏ID
    private Integer categoryId;
    //用户ID
    private Integer userId;
    //技能评分图片
    private String gradePicUrl;
    //技能描述
    private String description;
    //状态(0未认证,1认证中，2正常，3冻结)
    private Integer status;

    private String mobile;

    private String categoryName;

    private Date createTime;
    private Date updateTime;

    private Integer approveCount;

}
