package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  OrderStatusEnum{

    NON_PAYMENT(100,"待付款"),
    WAIT_SERVICE(200,"等待陪玩"),
    SERVICING(210,"陪玩中"),
    CHECK(240,"待验收"),
    APPEALING(300,"申诉中"),
    SERVER_CANCEL(400,"订单关闭"),//陪玩师取消订单
    USER_CANCEL(401,"取消订单"),//用户取消订单
    COMPLETE(500,"陪玩成功"),//用户验收订单
    ADMIN_COMPLETE(501,"陪玩成功"),//管理员强制完成订单
    ADMIN_REFUND(600,"协商处理");//管理员退款用户

    private Integer status;
    private String msg;






}
