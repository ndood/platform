package com.fulu.game.core.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/9/29 13:19.
 * @Description:
 */
@Data
@Builder
public class UserTechAuthEntity  implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 技能认证id */
    private Integer techAuthId;
    /** 技能分类id */
    private Integer categoryId;
    /** 技能名称 */
    private String name;
    /** 状态(0未认证,1认证中，2正常，3冻结) */
    private Integer status;
    /** 状态描述 */
    private String statusStr;
    /** 拒绝原因 */
    private String reason;
}
