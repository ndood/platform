package com.fulu.game.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户建议表
 *
 * @author yanbiao
 * @date 2018-07-02 11:03:20
 */
@Data
public class Advice implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    private Integer id;
    //用户id
    private Integer userId;
    //昵称
    private String nickname;
    //联系方式
    private String contact;
    //建议内容
    private String content;
    //状态(0待处理,1已处理,2标记)
    private Integer status;
    //管理员id
    private Integer adminId;
    //管理员名字
    private String adminName;
    //备注
    private String remark;
    //生成(反馈)时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
    //修改时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

}
