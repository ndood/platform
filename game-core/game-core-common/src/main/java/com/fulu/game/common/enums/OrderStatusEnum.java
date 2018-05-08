package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    SYSTEM_CLOSE(100, "订单关闭"),//系统关闭订单
    USER_CANCEL(101, "订单关闭"),//用户取消订单
    SERVER_CANCEL(160, "订单关闭"),//陪玩师取消订单
    NON_PAYMENT(200, "待付款"),
    WAIT_SERVICE(210, "等待陪玩"),//已付款
    SERVICING(220, "陪玩中"),
    CHECK(300, "等待验收"),
    APPEALING(400, "申诉中"),
    ADMIN_REFUND(410, "申诉：全额退款"),//管理员退款用户
    ADMIN_NEGOTIATE(420, "申诉：协商处理"),//管理员处理订单部分退款
    COMPLETE(500, "待评价"),//用户验收订单
    SYSTEM_COMPLETE(501, "待评价"),//系统自动完成订单
    ADMIN_COMPLETE(502, "申诉：订单完成"),//管理员强制完成订单
    ALREADY_APPRAISE(600, "已评价");//已评价


    private Integer status;
    private String msg;

    public static String getMsgByStatus(Integer status) {
        for (OrderStatusEnum statusEnum : OrderStatusEnum.values()) {
            if (statusEnum.status.equals(status)) {
                return statusEnum.msg;
            }
        }
        return null;
    }


}
