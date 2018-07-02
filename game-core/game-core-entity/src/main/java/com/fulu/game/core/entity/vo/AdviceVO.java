package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.Advice;
import lombok.Data;

/**
 * 用户建议表
 *
 * @author yanbiao
 * @date 2018-07-02 11:03:20
 */
@Data
public class AdviceVO extends Advice {

    private String startTime;
    private String endTime;

    private String urls;

}
