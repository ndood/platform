package com.fulu.game.play.aop;

import com.fulu.game.common.enums.UserScoreEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: 用户积分注解定义
 * @Author: Gong ZeChun
 * @Date: 2018/7/16 15:38
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface UserScore {
    UserScoreEnum type();
}
