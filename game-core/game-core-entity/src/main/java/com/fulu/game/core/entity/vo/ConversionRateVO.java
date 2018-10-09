package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.ConversionRate;
import lombok.Builder;
import lombok.Data;


/**
 * 转换率统计表
 *
 * @author shijiaoyun
 * @date 2018-09-25 11:52:10
 */
@Data
public class ConversionRateVO  extends ConversionRate {

    /** 查询开始时间 */
    private String beginTime;
    /** 查询结束时间 */
    private String endTime;
    /** 订单状态列表 */
    private Integer[] statusList;
    /** 订单是否已支付（1：已支付；0：未支付） */
    private Integer isPay;
    /** 是否复购人数（1：是；0：否） */
    private Integer isRepeact;
}
