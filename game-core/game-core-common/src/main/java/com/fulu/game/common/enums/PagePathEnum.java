package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PagePathEnum {

    TECH_SHARE_CARD("pages/c/card/card","陪玩师技能名片loadpage"),
    TECH_AUTH_CARD("pages/a/card/card","技能认证分享loadpage"),
    IM_MSG("pages/imsg/imsg","im消息列表页面"),
    ORDER_RECEIVING_SETTING("pages/a/setting/setting","接单设置页面"),
    COUPON_LIST("pages/c/coupon/coupon?backHome=true","优惠券列表页"),
    SERVICE_USER_VERIFY("pages/a/verify/verify","陪玩师认证页面"),
    USER_ORDER_LIST("pages/c/order/order?refresh=true","用户订单列表"),
    SERVICE_USER_ORDER_LIST("pages/a/order/order?refresh=true","陪玩师订单列表"),
    PUSH_PAGE("pages/push/push","小程序推送中转页面"),
    MARKET_ORDER_PAGE("pages/a/market/market","集市订单列表"),
    ORDER_LIST_PAGE("pages/order/list/list","订单列表");


    //跳转页面路径
    private String pagePath;
    //路径描述
    private String content;
}
