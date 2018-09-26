package com.fulu.game.core.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fulu.game.core.entity.Product;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserTechAuth;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 用户表
 *
 * @author wangbin
 * @date 2018-04-20 11:12:12
 */
@Data
public class UserVO extends User {

    //推送时间间隔
    private Float pushTimeInterval;

    private Date startTime;

    private Date endTime;
    //注册来源名
    private String sourceName;

    private BigDecimal paySum;
    private Integer orderCount;

    /**
     * 优惠券发放状态： 0：发放失败（默认）；1：发放成功
     */
    private Integer coupouStatus;

    @JsonIgnore
    private String orderBy;
    
    //未读消息数
    private long unreadCount;

    /** 用户兴趣 */
    private String interests;
    /** 用户职业 */
    private String profession;
    /** 用户简介 */
    private String about;
    /** 用户相册图片集 */
    private String[] picUrls;
    /** 用户视频地址 */
    private String videoUrl;
    /** 用户接单节能 */
    private List<Product> userProducts;
    /** 用户最新动态列表 */
    private List<DynamicVO> newestDynamics;
    /** 是否已关注（1：是；0：否） */
    private int isAttention = 0;
    /** 关注用户数 */
    private int attentions = 0;
    /** 粉丝数 */
    private int fans = 0;
    /** 用户月收入 */
    private BigDecimal monthIncome;
    // 来访次数
    private Integer accessCount;

}
