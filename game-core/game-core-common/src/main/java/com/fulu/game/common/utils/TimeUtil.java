package com.fulu.game.common.utils;

import cn.hutool.core.date.DateUtil;

import java.util.Date;

public class TimeUtil {

    private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String defaultFormat(Date date){
        return DateUtil.format(date,DEFAULT_FORMAT);
    }
}
