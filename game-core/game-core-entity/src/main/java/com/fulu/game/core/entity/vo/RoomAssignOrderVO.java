package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.RoomAssignOrder;
import lombok.Data;

@Data
public class RoomAssignOrderVO extends RoomAssignOrder {


    private String statusStr;


    public String getStatusStr() {
        if(getStatus()!=null&&getStatus()){
            return "进行中";
        }else{
            return "已结束";
        }
    }
}
