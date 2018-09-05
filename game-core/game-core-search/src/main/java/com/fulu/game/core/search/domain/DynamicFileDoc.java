package com.fulu.game.core.search.domain;

import lombok.Data;
import lombok.ToString;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/8/24 14:53.
 * @Description:
 */
@Data
@ToString
public class DynamicFileDoc {
    /** 文件链接地址 */
    private String url;
    /** 文件类型(1：图片；2：视频) */
    private Integer type;
    /** 播放次数 */
    private Long playCount;
    /** 图片宽度 */
    private Integer width;
    /** 图片高度 */
    private Integer height;
}
