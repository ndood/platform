package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.FenqileReconRecord;
import lombok.Data;

import java.util.Date;


/**
 * 分期乐对账记录表
 *
 * @author Gong Zechun
 * @date 2018-08-15 20:26:58
 */
@Data
public class FenqileReconRecordVO extends FenqileReconRecord {

    /**
     * 对账开始时间
     */
    private Date processStartTime;

    /**
     * 对账结束时间
     */
    private Date processEndTime;

    /**
     * 订单区间
     */
    private String orderInterval;
}
