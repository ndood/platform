package com.fulu.game.core.entity.vo.requestVO;

import lombok.Data;

import java.util.Date;

/**
 * @author 2018.4.29
 * 管理员订单列表查询参数VO
 */
@Data
public class OrderReqVO {
    private Integer pageSize = 15;//每页条数，默认15条
    private Integer pageNum;//当前第几页
    private String orderNo;//订单号
    private String userMobile;//玩家用户手机号
    private Integer status;//订单状态
    private Integer categoryId;//游戏id（内容id）
    private String startTime;
    private String endTime;
    private String orderBy = "id DESC";
    private Integer[] statusList;
}
