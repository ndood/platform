package com.fulu.game.common;

import java.math.BigDecimal;

public interface Constant {

    Integer DEF_PID = 0;  //默认的父类ID

    //手机验证码缓存时间
    Long VERIFYCODE_CACHE_TIME_DEV = 5 * 60L;
    Long VERIFYCODE_CACHE_TIME_DEP = 1 * 60L;

    Long TIME_HOUR_TOW = 2*3600L;

    Long TIME_MINUTES_FIFTEEN = 15*60L;

    //手机验证码限定次数和限定时间
    Integer MOBILE_CODE_SEND_TIMES_DEV = 20;
    Integer MOBILE_CODE_SEND_TIMES_DEP = 3;
    Long MOBILE_CACHE_TIME_DEV = 30 * 60L;


    //默认评价分
    BigDecimal DEFAULT_SCORE_AVG = new BigDecimal(4);
    //默认余额
    BigDecimal DEFAULT_BALANCE = new BigDecimal(0.00);
    //默认认证通过需要的认可数
    Integer DEFAULT_APPROVE_COUNT = 5;

    String DEFAULT_SPLIT_SEPARATOR = ",";
    Integer TECH_CARD_WIDTH = 400;
    Integer TECH_CARD_HEIGHT = 600;
}
