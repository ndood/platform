package com.fulu.game.core.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fulu.game.core.entity.User;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

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

    @JsonIgnore
    private String orderBy = "u.create_time desc";

}
