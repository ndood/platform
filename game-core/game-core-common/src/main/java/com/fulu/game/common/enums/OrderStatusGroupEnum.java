package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum  OrderStatusGroupEnum {

    USER_ALL("全部","USER",0,null),
    USER_NON_PAYMENT("待付款","USER",2,new Integer[]{200}),
    USER_WAIT_SERVICE("等待陪玩","USER",3,new Integer[]{210}),
    USER_SERVICING("陪玩中","USER",4,new Integer[]{220}),
    USER_APPEALING("申诉中","USER",5,new Integer[]{400}),
    USER_CHECK("等待验收","USER",6,new Integer[]{300}),
    USER_COMPLETE("订单完成","USER",7,new Integer[]{500,410,420,501,502,600}),
    USER_CLOSE("订单关闭","USER",1,new Integer[]{100,101,160}),

    SERVER_ALL("全部","SERVER",10,new Integer[]{160,210,220,400,300,500,410,420,501,502,600}),
    SERVER_WAIT_SERVICE("等待陪玩","SERVER",12,new Integer[]{210}),
    SERVER_SERVICING("陪玩中","SERVER",13,new Integer[]{220}),
    SERVER_APPEALING("申诉中","SERVER",14,new Integer[]{400}),
    SERVER_CHECK("等待验收","SERVER",15,new Integer[]{300}),
    SERVER_COMPLETE("订单完成","SERVER",16,new Integer[]{500,410,420,501,502,600}),
    SERVER_CLOSE("订单关闭","SERVER",11,new Integer[]{160}),

    ALL_NORMAL_COMPLETE("陪玩师成功完成订单","ALL",100,new Integer[]{500,501,502,600});


    private String name;
    private String type;
    private Integer value;
    private Integer[] statusList;


    public static Integer[] getByValue(Integer value){
        for(OrderStatusGroupEnum groupEnum : OrderStatusGroupEnum.values()){
            if(groupEnum.getValue().equals(value)){
               return groupEnum.statusList;
            }
        }
        return null;
    }
}
