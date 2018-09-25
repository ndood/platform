package com.fulu.game.core.service;

import java.util.Map;

/**
 * 午夜场服务类接口
 *
 * @author Gong ZeChun
 * @date 2018/9/17 14:53
 */
public interface MidnightService {

    void setConfig(String startTime, String endTime);

    Map<String, Object> getConfig();
}