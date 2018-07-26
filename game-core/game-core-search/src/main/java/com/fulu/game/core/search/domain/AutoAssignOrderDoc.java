package com.fulu.game.core.search.domain;

import io.searchbox.annotations.JestId;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class AutoAssignOrderDoc {

    //商品ID
    @JestId
    private Integer id;

    //陪玩师ID
    private Integer userId;

    //权重
    private Integer weight;

    //是否允许指派订单
    private Boolean allowAssignOrder;

    //指派接单时间
    private Date assignTime;

    //正在派单中
    private Boolean assignOrderIng;

}
