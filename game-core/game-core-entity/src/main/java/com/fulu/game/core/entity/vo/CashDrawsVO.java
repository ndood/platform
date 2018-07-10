package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.CashDraws;
import lombok.Data;

import java.util.Date;

/**
 * @author yanbiao
 * @date 2018-04-24 16:45:40
 */
@Data
public class CashDrawsVO extends CashDraws {

    /**
     * 申请开始时间
     */
    private Date createStartTime;

    /**
     * 申请结束时间
     */
    private Date createEndTime;
}
