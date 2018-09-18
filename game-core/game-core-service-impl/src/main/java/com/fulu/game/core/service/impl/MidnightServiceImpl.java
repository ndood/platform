package com.fulu.game.core.service.impl;

import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.core.service.MidnightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * todo：描述文字
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
}
