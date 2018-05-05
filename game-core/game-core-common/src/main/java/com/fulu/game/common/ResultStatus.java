package com.fulu.game.common;

/**
 * Author: koabs
 * 8/22/16.
 * 返回结果状态
 */
public class ResultStatus {

    //接口正常返回
    public static Integer SUCCESS = 200;

    //系统内部错误,需要前端捕获的
    public static Integer ERROR = 500;

    //用户未登陆
    public static Integer NOLOGIN = 501;

    //新用户待绑定
    public static Integer NEWUSER = 201;

    //手机号已注册
    public static Integer MOBILE_DUPLICATE = 202;

    //IM用户注册失败
    public static Integer IM_REGIST_FAIL= 203;

}
