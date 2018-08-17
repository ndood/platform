package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum OrderStatusGroupEnum {

    USER_ALL("全部", "USER", 0, null),
    USER_NON_PAYMENT("待付款", "USER", 2, new Integer[]{200}),
    USER_WAIT_SERVICE("等待陪玩", "USER", 3, new Integer[]{210}),
    USER_SERVICING("陪玩中", "USER", 4, new Integer[]{220}),
    USER_APPEALING("申诉中", "USER", 5, new Integer[]{400, 401}),
    USER_CHECK("等待验收", "USER", 6, new Integer[]{300}),
    USER_COMPLETE("订单完成", "USER", 7, new Integer[]{500, 410, 420, 501, 502, 600}),
    USER_CLOSE("订单关闭", "USER", 1, new Integer[]{100, 101, 110, 160}),

    SERVER_ALL("全部", "SERVER", 10, new Integer[]{160, 210, 220, 400, 300, 500, 410, 420, 501, 502, 600}),
    SERVER_WAIT_SERVICE("等待陪玩", "SERVER", 12, new Integer[]{210}),
    SERVER_SERVICING("陪玩中", "SERVER", 13, new Integer[]{220}),
    SERVER_APPEALING("申诉中", "SERVER", 14, new Integer[]{400, 401}),
    SERVER_CHECK("等待验收", "SERVER", 15, new Integer[]{300}),
    SERVER_COMPLETE("订单完成", "SERVER", 16, new Integer[]{500, 410, 420, 501, 502, 600}),
    SERVER_CLOSE("订单关闭", "SERVER", 11, new Integer[]{110, 160}),

    ADMIN_ALL("全部", "ADMIN", 20, null),
    ADMIN_CLOSE("订单关闭", "ADMIN", 21, new Integer[]{100, 101, 102, 110, 160}),
    ADMIN_NON_PAYMENT("待付款", "ADMIN", 22, new Integer[]{200}),
    ADMIN_WAIT_SERVICE("等待陪玩", "ADMIN", 23, new Integer[]{210, 213}),
    ADMIN_SERVING("陪玩中", "ADMIN", 24, new Integer[]{220}),
    ADMIN_CHECK("等待验收", "ADMIN", 25, new Integer[]{300}),
    ADMIN_CONSULTING("协商中", "ADMIN", 26, new Integer[]{350, 352, 354, 415, 416}),
    ADMIN_APPEALING("仲裁中", "ADMIN", 27, new Integer[]{400, 401, 410, 420, 502}),
    ADMIN_COMPLETE("订单完成", "ADMIN", 28, new Integer[]{500, 410, 415, 416, 420, 501, 502, 600}),


    MARKET_ALL("全部", "MARKET", 30, new Integer[]{210, 220, 300, 500, 501, 502, 600}),
    MARKET_WAIT_SERVICE("等待陪玩", "MARKET", 31, new Integer[]{210}),
    MARKET_SERVING("陪玩中", "MARKET", 32, new Integer[]{220}),
    MARKET_CHECK("等待验收", "MARKET", 33, new Integer[]{300}),
    MARKET_COMPLETE("订单完成", "MARKET", 34, new Integer[]{500, 501, 502, 600}),


    MARKET_ORDER_REMARK_VISIBLE("接单者可见订单", "REMARK_VISIBLE", 200, new Integer[]{210, 220, 300}),
    ALL_NORMAL_COMPLETE("陪玩师成功完成订单", "ALL", 100, new Integer[]{500, 501, 502, 600}),


    CONSULT_ALL("所有协商状态", "ALL", 301, new Integer[]{350, 352, 415, 416}),
    APPEAL_ALL("所有仲裁状态", "ALL", 302, new Integer[]{400, 401, 410, 420, 502}),

    ORDER_CONTACT_INVISIBLE("联系方式可见状态", "ALL", 201, new Integer[]{100, 102, 110, 101, 160, 200, 210}),

    RECON_ALL("分期乐对账状态", "ALL", 35, new Integer[]{500, 410, 420, 501, 502, 600, 415, 416});

    private String name;
    private String type;
    private Integer value;
    private Integer[] statusList;


    public static Integer[] getByValue(Integer value) {
        for (OrderStatusGroupEnum groupEnum : OrderStatusGroupEnum.values()) {
            if (groupEnum.getValue().equals(value)) {
                return groupEnum.statusList;
            }
        }
        return null;
    }
}
