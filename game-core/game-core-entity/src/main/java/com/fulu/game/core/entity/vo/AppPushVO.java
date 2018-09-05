package com.fulu.game.core.entity.vo;

public class AppPushVO {


    enum PushRoute{

        PAGE_INDEX("andorid_route","ios_route","xxx");

        private String androidRoute;
        private String iosRoute;


        PushRoute(String andoridRoute, String iosRoute, String type) {
        }
    }





}

