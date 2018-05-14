package com.fulu.game.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 分享文案表
 *
 * @author yanbiao
 * @date 2018-05-14 11:46:29
 */
@Data
public class Sharing implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    private Integer id;
    //分享类型
    private Integer shareType;
    //性别
    private Integer gender;
    //文案内容
    private String content;
    //是否启用(默认1启用，0不启用)
    private Boolean status;
    //记录生成时间
    private Date createTime;
    //记录修改时间
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateTime() {
        return createTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getUpdateTime() {
        return updateTime;
    }

}
