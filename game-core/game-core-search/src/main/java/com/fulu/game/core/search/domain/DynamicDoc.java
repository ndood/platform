package com.fulu.game.core.search.domain;

import io.searchbox.annotations.JestId;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.BitSet;
import java.util.Date;
import java.util.List;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/8/24 14:37.
 * @Description: 动态DOC
 */
@Data
@ToString
public class DynamicDoc {
    @JestId
    private Long id;
    private Integer userId;
    /** '评论用户头像URL（冗余字段，提高查询效率）' */
    private String userHeadUrl;
    /** 评论用户昵称（冗余字段，提高查询效率） */
    private String userNickname;
    /** 评论用户性别(冗余字段，默认0：不公开；1：男；2：女) */
    private Integer userGender;
    /** 评论用户年龄(冗余字段) */
    private Integer userAge;
    /** 动态类型（0：文字；1：图片；2：视频） */
    private Integer type;
    /** 技能id */
    private Integer techInfoId;
    /** 技能名称 */
    private String techInfoName;
    /** 技能价格 */
    private BigDecimal techInfoPrice;
    /** 技能单位 */
    private String techInfoUnit;
    /** 动态内容 */
    private String content;
    /** 城市编码（用于查询附近的动态） */
    private String cityCode;
    /** 城市名称（用于查询附近的动态） */
    private String cityName;
    /** 地理位置hash（用于查询附近的动态） */
    private String geohash;
    /** 地理位置hash（用于查询附近的动态，去掉geohash后两位） */
    private String gethashShort;
    /** 经度（用于计算距离） */
    private double lon;
    /** 纬度（用于计算距离） */
    private double lat;
    /** 是否置顶（1：是；0：否） */
    private Integer isTop;
    /** 打赏次数 */
    private Long rewards;
    /** 点赞次数 */
    private Long likes;
    /** 评论次数，预留 */
    private Long comments;
    /** 点击次数 */
    private Long clicks;
    /** 是否热门（1：是；0：否） */
    private Integer isHot;
    /** 创建时间 */
    private Date createTime;
    /** 修改时间（预留，暂不确定是否有修改功能） */
    private Date updateTime;
    /** 动态状态（1：有效；0：无效） */
    private Integer status;
    /** 动态相关联文件 */
    private List<DynamicFileDoc> files;
    /** 点赞用户id */
    private BitSet likeUserIds;
    /** 是否已点赞（1：是；0：否） */
    private int isLike;
}
