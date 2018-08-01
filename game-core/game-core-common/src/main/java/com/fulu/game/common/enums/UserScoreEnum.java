package com.fulu.game.common.enums;

import com.fulu.game.common.Constant;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 用户积分枚举类
 * @Author: Gong ZeChun
 * @Date: 2018/7/16 16:17
 */
@Getter
@AllArgsConstructor
public enum UserScoreEnum {

    USER_LOGIN(Constant.USER_LOGIN, 1),

    IM_REPLY(Constant.IM_REPLY, 0),
    IM_REPLY_DELAY_0_TO_5(Constant.IM_REPLY, 1),
    IM_REPLY_DELAY_5_TO_60(Constant.IM_REPLY, 0),
    IM_REPLY_DELAY_60_TO_120(Constant.IM_REPLY, -2),
    IM_REPLY_DELAY_120_TO_360(Constant.IM_REPLY, -5),
    IM_REPLY_DELAY_360_TO_1440(Constant.IM_REPLY, -8),
    IM_REPLY_DELAY_LONGGER_1440(Constant.IM_REPLY, -10),

    ACCEPT_ORDER(Constant.ACCEPT_ORDER, 0),
    ACCEPT_ORDER_DELAY_30_TO_60(Constant.ACCEPT_ORDER_DELAY_30_TO_60, -2),
    ACCEPT_ORDER_DELAY_60_TO_360(Constant.ACCEPT_ORDER_DELAY_60_TO_360, -5),
    ACCEPT_ORDER_DELAY_360_TO_1440(Constant.ACCEPT_ORDER_DELAY_360_TO_1440, -8),
    ACCEPT_ORDER_DELAY_LONGGER_1440(Constant.ACCEPT_ORDER_DELAY_LONGGER_1440, -10),

    USER_COMMENT(Constant.USER_COMMENT, 0),
    USER_COMMENT_1_STAR(Constant.USER_COMMENT_1_STAR, -2),
    USER_COMMENT_2_STAR(Constant.USER_COMMENT_2_STAR, -1),
    USER_COMMENT_3_STAR(Constant.USER_COMMENT_3_STAR, 0),
    USER_COMMENT_4_STAR(Constant.USER_COMMENT_4_STAR, 1),
    USER_COMMENT_5_STAR(Constant.USER_COMMENT_5_STAR, 2),

    //全额退款给用户
    FULL_RESTITUTION(Constant.FULL_RESTITUTION, -20),
    //仲裁
    NEGOTIATE(Constant.NEGOTIATE, -10),
    //协商
    CONSULT(Constant.CONSULT, -5),

    /**
     * 用户正常完成订单
     */
    USER_FINISH_ORDER(Constant.USER_FINISH_ORDER, -1),

    /**
     * 陪玩师正常完成订单
     */
    SERVICE_USER_FINISH_ORDER(Constant.SERVICE_USER_FINISH_ORDER, -1),

    /**
     * 用户取消订单
     */
    USER_CANCEL_ORDER(Constant.USER_CANCEL_ORDER, -5),

    /**
     * 陪玩师取消订单
     */
    SERVICE_USER_CANCEL_ORDER(Constant.SERVICE_USER_CANCEL_ORDER, -10);

    /**
     * 描述
     */
    private String description;

    /**
     * 分数
     */
    private Integer score;
}
