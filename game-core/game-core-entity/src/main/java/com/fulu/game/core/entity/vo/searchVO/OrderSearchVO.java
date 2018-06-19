package com.fulu.game.core.entity.vo.searchVO;

import lombok.Data;

/**
 * @author 2018.4.29
 * 管理员订单列表查询参数VO
 */
@Data
public class OrderSearchVO {

    private String orderNo;//订单号
    private Integer channelId; //渠道商ID
    private String userMobile;//玩家用户手机号
    private Integer status;//订单状态
    private Integer categoryId;//游戏id（内容id）
    private String startTime;
    private String endTime;
    private Integer[] statusList;
}
