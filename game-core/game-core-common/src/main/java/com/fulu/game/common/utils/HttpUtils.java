package com.fulu.game.common.utils;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import java.util.Map;

/**
 * HTTP 请求工具类
 *
 * @author yanbaio
 * @date 2018.5.10
 */
public class HttpUtils {

    /**
     * 简单get
     *
     * @param url
     * @param paramMap
     * @return
     */
    public static String get(String url, Map paramMap) {
        return get(url, paramMap, null).body();
    }

    /**
     * 简单post
     * @param url
     * @param paramMap
     * @return
     */
    public static String post(String url, Map paramMap) {
        return post(url, paramMap, null).body();
    }

    /**
     * get请求
     *
     * @param url
     * @param paramMap
     * @return
     */
    public static HttpResponse get(String url, Map paramMap, Map header) {
        return HttpRequest.get(url)
                .addHeaders(header)
                .form(paramMap)
                .execute();
    }

    /**
     * post请求
     *
     * @param url
     * @param paramMap
     * @return
     */
    public static HttpResponse post(String url, Map paramMap, Map header) {
        return HttpRequest.post(url)
                .addHeaders(header)
                .form(paramMap)
                .execute();

    }


    public static HttpResponse post(String url, String body , Map header) {
        return HttpRequest.post(url)
                .addHeaders(header)
                .body(body)
                .execute();
    }

}
