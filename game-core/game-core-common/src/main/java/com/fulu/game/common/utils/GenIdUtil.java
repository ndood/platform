package com.fulu.game.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.fulu.game.common.properties.Config;

import java.util.Date;
import java.util.UUID;

/**
 * 生成唯一性id的工具类
 */
public class GenIdUtil {

    private static final String SALT = "";
    private static final String CHANNEL_PRE = "KHPW";


    private static String getEvnPrefix() {
        Config config = ApplicationContextRegister.getBean("configProperties",Config.class);
        return config.getEvn().getPrefix();
    }

    /**
     * 生成token
     *
     * @return
     */
    public static String GetToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成uuid
     *
     * @return
     */
    public static String GetGUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 生成订单号
     *
     * @return
     */
    public static String GetOrderNo() {
        String date = DateUtil.format(new Date(), "yyMMdd");
        String randomNum = RandomUtil.randomNumbers(6);
        String evn = getEvnPrefix();
        if("PROD".equals(evn)){
            return date + randomNum;
        }else{
            return evn+date + randomNum;
        }

    }

    /**
     * 生成优惠券编码
     *
     * @return
     */
    public static String GetCouponNo() {
        String date = DateUtil.format(new Date(), "yyMMdd");
        String randomStr = RandomUtil.randomNumbers(7);
        return date + randomStr;
    }

    /**
     * 生成分享图随机码
     *
     * @return
     */
    public static String GetImgNo() {
        return RandomUtil.randomNumbers(7);
    }

    /**
     * 生成渠道商appid
     *
     * @return
     */
    public static String getAppid() {
        return CHANNEL_PRE + "#" + System.currentTimeMillis();
    }

    /**
     * 生成渠道商appkey(实际是token)
     *
     * @return
     */
    public static String getAppkey() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * cdk序列号
     *
     * @return
     */
    public static String getCdkSeries() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String get7RandomNumber() {
        return null;
    }

}
