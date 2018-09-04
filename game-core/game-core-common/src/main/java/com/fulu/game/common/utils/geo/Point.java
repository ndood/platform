package com.fulu.game.common.utils.geo;

import lombok.Data;
/**
 * @Author: shijiaoyun.
 * @Date: 2018/8/24 10:06.
 * @Description: 经纬度
 */
@Data
public class Point {
    private double lon;
    private double lat;
    public Point(){
        super();
    }

    public Point(double lon, double lat){
        super();
        this.lon = lon;
        this.lat = lat;
    }
}
