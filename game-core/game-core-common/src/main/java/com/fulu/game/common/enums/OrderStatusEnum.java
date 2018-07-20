package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {


    UNPAY_ORDER_CLOSE(100, "订单关闭",""),//未付款订单取消或者关闭
    SYSTEM_CLOSE(102, "订单取消",""),//系统关闭订单
    ADMIN_CLOSE(110, "订单取消",""),//管理员关闭订单
    USER_CANCEL(101, "订单取消",""),//用户取消订单
    SERVER_CANCEL(160, "订单取消",""),//陪玩师取消订单
    NON_PAYMENT(200, "待付款","尽快付款享受服务哦##超过24小时未付款,订单将自动关闭。"),
    WAIT_SERVICE(210, "等待接单","等待陪玩师接单##超过将自动取消订单"),//已付款
    ALREADY_RECEIVING(213, "等待开始","等待陪玩师联系老板开始服务##超时将退款给老板"),//陪玩师已接单
    SERVICING(220, "陪玩中","服务完成后进行验收与确认##若陪玩师提交验收，用户24小时内未确认，将自动验收"), //服务进行中
    CHECK(300, "等待验收","服务完成后，请确认完成服务##指定时间内未验收，将自动验收"),   //陪玩师提交验收
    CONSULTING(350,"协商中","老板申请退款、等待陪玩师回应##超时将同意退款"),
    CONSULT_REJECT(352,"协商拒绝","老板拒绝退款，等待用户回应##超时将取消退款申请"),
    CONSULT_CANCEL(354,"协商取消",""),
    APPEALING(400, "仲裁中","客服即将介入，请上传凭证##请在2个小时内上传凭证"), //用户申诉订单
    APPEALING_ADMIN(401, "仲裁中","客服即将介入，请上传凭证##请在2个小时内上传凭证"), //管理员申诉订单
    ADMIN_REFUND(410, "仲裁完成:老板胜诉",""),//管理员退款用户
    CONSULT_COMPLETE(415, "协商完成",""),//陪玩师退钱给用户
    SYSTEM_CONSULT_COMPLETE(416, "协商完成",""),//陪玩师退钱给用户
    ADMIN_NEGOTIATE(420, "仲裁完成:协商处理",""),//管理员处理订单一部分用户一部分陪玩师
    COMPLETE(500, "待评价","等待老板进行评价"),//用户验收订单
    SYSTEM_COMPLETE(501, "待评价","等待老板进行评价"),//系统自动完成订单
    ADMIN_COMPLETE(502, "仲裁完成:陪玩师胜诉",""),//管理员强制完成订单
    ALREADY_APPRAISE(600, "已评价","");//已评价


    private Integer status;
    private String msg;
    private String note;

    public static String getMsgByStatus(Integer status) {
        for (OrderStatusEnum statusEnum : OrderStatusEnum.values()) {
            if (statusEnum.status.equals(status)) {
                return statusEnum.msg;
            }
        }
        return null;
    }

    public static String getNoteByStatus(Integer status) {
        for (OrderStatusEnum statusEnum : OrderStatusEnum.values()) {
            if (statusEnum.status.equals(status)) {
                return statusEnum.note;
            }
        }
        return null;
    }


}
