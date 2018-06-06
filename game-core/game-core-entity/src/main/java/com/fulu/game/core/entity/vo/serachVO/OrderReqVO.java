package com.fulu.game.core.entity.vo.serachVO;

import lombok.Data;

/**
 * @author 2018.4.29
 * 管理员订单列表查询参数VO
 */
@Data
public class OrderReqVO {

    private String orderNo;//订单号
    private String userMobile;//玩家用户手机号
    private Integer status;//订单状态
    private Integer categoryId;//游戏id（内容id）
    private String startTime;
    private String endTime;
    private Integer[] statusList;
}
