package com.fulu.game.core.service;

import java.util.Map;

/**
 * todo：描述文字
 *
 * @author Gong ZeChun
 * @date 2018/9/17 14:53
 */
public interface MidnightService {

    void setConfig(String startTime, String endTime);

    Map<String, Object> getConfig();
}