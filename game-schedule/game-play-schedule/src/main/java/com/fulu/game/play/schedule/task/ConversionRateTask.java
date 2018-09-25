package com.fulu.game.play.schedule.task;

import com.fulu.game.core.service.ConversionRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/9/25 14:24.
 * @Description:
 */
@Component
@Slf4j
public class ConversionRateTask {

    @Autowired
    private ConversionRateService conversionRateService;

    /**
     * 统计转化率
     * 每天凌晨2点开始统计
     */
    @Scheduled(cron = "0 0 2 * * ? ")
    public void statisticsConversionRate() {
        log.info("开始统计转化率");
        conversionRateService.statisticsConversionRate();
        log.info("结束统计转化率");
    }

}
