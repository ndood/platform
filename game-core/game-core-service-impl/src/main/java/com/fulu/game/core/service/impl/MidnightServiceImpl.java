package com.fulu.game.core.service.impl;

import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.core.service.MidnightService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 午夜场服务类
 *
 * @author Gong ZeChun
 * @date 2018/9/17 14:53
 */
@Service
@Slf4j
public class MidnightServiceImpl implements MidnightService {
    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    @Override
    public void setConfig(String startTime, String endTime) {
        redisOpenService.set(RedisKeyEnum.MIDNIGHT.generateKey(),
                startTime + Constant.DEFAULT_SPLIT_SEPARATOR + endTime,
                Boolean.TRUE);
    }

    @Override
    public Map<String, Object> getConfig() {
        String timeStr = redisOpenService.get(RedisKeyEnum.MIDNIGHT.generateKey());
        if (StringUtils.isBlank(timeStr)) {
            return null;
        }
        String startTime = timeStr.split(Constant.DEFAULT_SPLIT_SEPARATOR)[0];
        String endTime = timeStr.split(Constant.DEFAULT_SPLIT_SEPARATOR)[1];
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("startTime", startTime);
        resultMap.put("endTime", endTime);
        return resultMap;
    }
}
