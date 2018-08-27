package com.fulu.game.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 官方公告表
 *
 * @author Gong Zechun
 * @date 2018-08-27 18:09:53
 */
@Data
public class OfficialNotice implements Serializable {
    private static final long serialVersionUID = 1L;

    //
    private Integer id;
    //标题
    private String title;
    //内容
    private String content;
    //URL链接
    private String url;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;

}
