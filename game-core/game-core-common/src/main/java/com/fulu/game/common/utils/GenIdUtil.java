package com.fulu.game.common.utils;

import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.RandomUtil;

import java.util.Date;
import java.util.UUID;

/**
 * 生成唯一性id的工具类
 */
public class GenIdUtil {

    /**
     * 生成token
     *
     * @return
     */
    public static String GetGUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 生成订单号
     * @return
     */
    public static String GetOrderNo(){
        String date = DateUtil.format(new Date(),"yyMMdd");
        String randomNum = RandomUtil.randomNumbers(6);
        return date+randomNum;
    }




}
