package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.RoomCategory;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * 房间分类表
 *
 * @author wangbin
 * @date 2018-10-07 00:25:52
 */
@Data
public class RoomCategoryVO  extends RoomCategory {


    private List<RoomVO> roomList = new ArrayList<>();
}
