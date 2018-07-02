package com.fulu.game.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 提建议-文件
 *
 * @author yanbiao
 * @date 2018-07-02 13:37:16
 */
@Data
public class AdviceFile implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    private Integer id;
    //关联的建议id
    private Integer adviceId;
    //图片地址
    private String url;
    //生成时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

}
