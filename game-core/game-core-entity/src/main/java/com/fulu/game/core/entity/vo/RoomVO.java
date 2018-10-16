package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.Room;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


/**
 * 聊天室
 *
 * @author wangbin
 * @date 2018-10-07 00:25:52
 */
@Data
public class RoomVO  extends Room {


    //房间分类名称
    private String roomCategoryName;

    //房间人数
    private Long people;

    //接单率
    private BigDecimal orderRate;

    //满意度评分
    private BigDecimal satisfy;


    @NotNull(message = "[手机号]字段不能为空")
    private String ownerMobile;

    @NotNull(message = "[房间类型]字段不能为空")
    private Integer template;

    @NotNull(message = "[房间分类]字段不能为空")
    private Integer roomCategoryId;




}
