package com.fulu.game.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 举报文件表
 *
 * @author Gong Zechun
 * @date 2018-08-27 13:56:48
 */
@Data
public class ReportFile implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    private Integer id;
    //举报id
    private Integer reportId;
    //图片路径
    private String url;
    //备注
    private String remark;
    //修改时间
    private Date updateTime;
    //创建时间
    private Date createTime;

}
