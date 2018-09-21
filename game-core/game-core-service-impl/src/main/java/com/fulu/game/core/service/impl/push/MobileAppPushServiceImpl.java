package com.fulu.game.core.service.impl.push;

import cn.hutool.core.util.StrUtil;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.vo.AppPushMsgVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MobileAppPushServiceImpl extends PushServiceImpl implements IBusinessPushService {


    public void pushMsg(AppPushMsgVO appPushMsgVO) {
        if (appPushMsgVO.getSendAll()) {
            pushMsg(appPushMsgVO.getTitle(), appPushMsgVO.getAlert(), appPushMsgVO.getExtras());
        } else {
            pushMsg(appPushMsgVO.getTitle(), appPushMsgVO.getAlert(), appPushMsgVO.getExtras(), appPushMsgVO.getUserIds());
        }
    }


    @Override
    public void receiveOrder(Order order) {
        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(order.getUserId())
                .title("订单消息")
                .alert(WechatTemplateMsgEnum.ORDER_TOUSER_AFFIRM_RECEIVE.getContent())
                .build();
        pushMsg(appPushMsgVO);
    }


    @Override
    public void remindReceive(Order order) {
        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(order.getServiceUserId())
                .title("订单消息")
                .alert(WechatTemplateMsgEnum.ORDER_TOSERVICE_REMIND_RECEIVE.getContent())
                .build();
        pushMsg(appPushMsgVO);
    }

    @Override
    public void remindStart(Order order) {
        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(order.getServiceUserId())
                .title("订单消息")
                .alert(WechatTemplateMsgEnum.ORDER_TOSERVICE_REMIND_START_SERVICE.getContent())
                .build();
        pushMsg(appPushMsgVO);
    }

    @Override
    public void serviceUserAcceptOrder(Order order) {
        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(order.getServiceUserId())
                .title("订单消息")
                .alert(WechatTemplateMsgEnum.POINT_TOSERVICE_ORDER_RECEIVING.getContent())
                .build();
        pushMsg(appPushMsgVO);
    }

    @Override
    public void start(Order order) {
        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(order.getUserId())
                .title("订单消息")
                .alert(WechatTemplateMsgEnum.ORDER_TOUSER_START_SERVICE.getContent())
                .build();
        pushMsg(appPushMsgVO);
    }

    @Override
    public void consult(Order order) {
        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(order.getServiceUserId())
                .title("订单消息")
                .alert(WechatTemplateMsgEnum.ORDER_TOSERVICE_CONSULT.getContent())
                .build();
        pushMsg(appPushMsgVO);
    }

    @Override
    public void rejectConsult(Order order) {
        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(order.getUserId())
                .title("订单消息")
                .alert(WechatTemplateMsgEnum.ORDER_TOUSER_CONSULT_REJECT.getContent())
                .build();
        pushMsg(appPushMsgVO);
    }

    @Override
    public void agreeConsult(Order order) {
        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(order.getUserId())
                .title("订单消息")
                .alert(WechatTemplateMsgEnum.ORDER_TOUSER_CONSULT_AGREE.getContent())
                .build();
        pushMsg(appPushMsgVO);
    }

    @Override
    public void cancelConsult(Order order) {
        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(order.getServiceUserId())
                .title("订单消息")
                .alert(WechatTemplateMsgEnum.ORDER_TOSERVICE_CONSULT_CANCEL.getContent())
                .build();
        pushMsg(appPushMsgVO);
    }

    @Override
    public void cancelOrderByServer(Order order) {
        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(order.getUserId())
                .title("订单消息")
                .alert(WechatTemplateMsgEnum.ORDER_TOUSER_REJECT_RECEIVE.getContent())
                .build();
        pushMsg(appPushMsgVO);
    }

    @Override
    public void cancelOrderByUser(Order order) {
        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(order.getServiceUserId())
                .title("订单消息")
                .alert(WechatTemplateMsgEnum.ORDER_TOSERVICE_ORDER_CANCEL.getContent())
                .build();
        pushMsg(appPushMsgVO);
    }

    @Override
    public void appealByServer(Order order) {
        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(order.getUserId())
                .title("订单消息")
                .alert(WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL.getContent())
                .build();
        pushMsg(appPushMsgVO);
    }

    @Override
    public void appealByUser(Order order) {
        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(order.getServiceUserId())
                .title("订单消息")
                .alert(WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL.getContent())
                .build();
        pushMsg(appPushMsgVO);
    }

    @Override
    public void checkOrder(Order order) {
        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(order.getUserId())
                .title("订单消息")
                .alert(WechatTemplateMsgEnum.ORDER_TOUSER_CHECK.getContent())
                .build();
        pushMsg(appPushMsgVO);
    }

    @Override
    public void orderPay(Order order) {
        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(order.getServiceUserId())
                .title("订单消息")
                .alert(WechatTemplateMsgEnum.ORDER_TOSERVICE_PAY.getContent())
                .build();
        pushMsg(appPushMsgVO);
    }

    @Override
    public void acceptOrder(Order order) {
        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(order.getUserId())
                .title("订单消息")
                .alert(WechatTemplateMsgEnum.ORDER_TOSERVICE_AFFIRM_SERVER.getContent())
                .build();
        pushMsg(appPushMsgVO);
    }


    @Override
    public void grantCouponMsg(int userId, String deduction) {
        String content = WechatTemplateMsgEnum.GRANT_COUPON.getContent();
        content = StrUtil.format(content, deduction);
        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(userId)
                .title("优惠券消息")
                .alert(content)
                .build();
        pushMsg(appPushMsgVO);
    }
}
