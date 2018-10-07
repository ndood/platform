package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.Room;
import lombok.Data;

import javax.validation.constraints.NotNull;


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
    private Integer people;

    @NotNull(message = "[手机号]字段不能为空")
    private String ownerMobile;

    @NotNull(message = "[房间类型]字段不能为空")
    private Integer template;

    @NotNull(message = "[房间分类]字段不能为空")
    private Integer roomCategoryId;




}
