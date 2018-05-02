package com.fulu.game.common;

public interface Constant {

    Integer DEF_PID = 0;  //默认的父类ID

    //手机验证码缓存时间
    Long VERIFYCODE_CACHE_TIME_DEV = 5 * 60L;
    Long VERIFYCODE_CACHE_TIME_DEP = 1 * 60L;

    //手机验证码限定次数和限定时间
    Integer MOBILE_CODE_SEND_TIMES_DEV = 20;
    Integer MOBILE_CODE_SEND_TIMES_DEP = 3;
    Long MOBILE_CACHE_TIME_DEV = 30 * 60L;

}
