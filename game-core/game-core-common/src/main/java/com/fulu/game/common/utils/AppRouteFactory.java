package com.fulu.game.common.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 构建app消息推送路由工厂方法
 */
public class AppRouteFactory {


    @Getter
    public enum RouteType{
        OFFICIAL_NOTICE("fulu://IM_GonggaoActivity","fulu://KHOfficialNoticeViewController"), //官方公告
        INDEX("androidRoute","iosRoute"), //首页
        DYNAMIC("fulu://DongTaiDetailActivity","fulu://KHDynamicDetailController"), //动态
        WEBVIEW("androidRoute","iosRoute");


        private String androidRoute;
        private String iosRoute;

        RouteType(String androidRoute, String iosRoute) {
            this.androidRoute =androidRoute;
            this.iosRoute = iosRoute;
        }
    }



    /**
     * 消息跳转路由定义
     * @return
     */
    public static Map<String,String> buildIndexRoute(){
        Map<String,String> route = newRoute(RouteType.INDEX);
        return route;
    }


    /**
     * 构建H5跳转路由
     * @param url
     * @return
     */
    public static Map<String,String> buildH5Route(String url){
        Map<String,String> route = newRoute(RouteType.WEBVIEW);
        route.put("url",url);
        return route;
    }

    /**
     * 构建官方公告跳转路由
     * @return
     */
    public static Map<String,String> buildOfficialNoticeRoute(){
        Map<String,String> route = newRoute(RouteType.OFFICIAL_NOTICE);
        return route;
    }

    /**
     * 消息跳转路由定义
     * @return
     */
    public static Map<String,String> buildDynamicRoute(Integer dynamicId){
        Map<String, String> map = new HashMap<>();
        map.put("id",dynamicId + "");
        Map<String,String> route = newRoute(RouteType.DYNAMIC,map);
        return route;
    }

    /**
     * 创建一个路由map
     * @param routeType
     * @return
     */
    private static Map<String,String> newRoute(RouteType routeType,Map<String,String> params){
        String paramStr = "";
        if(!params.isEmpty()){
            paramStr = "params="+ JSONObject.toJSONString(params);
        }
        Map<String,String> route = new HashMap<>();
        route.put("androidRoute",routeType.getAndroidRoute()+paramStr);
        route.put("iosRoute",routeType.getIosRoute()+paramStr);
        return route;
    }

    private static Map<String,String> newRoute(RouteType routeType){
        return newRoute(routeType,new HashMap<>());
    }

}
