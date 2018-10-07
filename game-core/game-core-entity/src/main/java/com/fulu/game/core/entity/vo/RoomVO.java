package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.Room;
import lombok.Data;


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


}
