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

    private Integer userId; //用户ID
    private Integer serviceUserId; //陪玩师ID
    private String userMobile;//玩家用户手机号
    private String serviceUserMobile;//陪玩师用户手机号

    private Integer status;//订单状态
    private Integer categoryId;//游戏id（内容id）
    private String startTime;
    private String endTime;
    private Integer[] statusList;
    //接单间隔左范围默认为1
    private Integer ltInterval;
    //接单间隔右范围
    private Integer gtInterval = 0;
}
