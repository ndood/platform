package com.fulu.game.core.service.impl.push;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderStatusDetails;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.AppPushMsgVO;
import com.fulu.game.core.service.OrderMsgService;
import com.fulu.game.core.service.OrderStatusDetailsService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.queue.AppPushContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
@Slf4j
public class MobileAppPushServiceImpl extends PushServiceImpl implements IBusinessPushService {

    @Autowired
    private AppPushContainer appPushContainer;
    @Autowired
    private OrderMsgService orderMsgService ;
    @Autowired
    private UserService userService;
    @Autowired
    protected OrderStatusDetailsService orderStatusDetailsService;


    /**
     * 执行推送方法
     * @param appPushMsgVO
     */
    public void pushMsg(AppPushMsgVO appPushMsgVO) {
        appPushContainer.add(appPushMsgVO);
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
        //【触电】用户名称提示您尽快对“王者荣耀”进行接单，请快速处理(如已处理，请忽略)
        Date beginTime = order.getBeginTime();
        if(beginTime==null){
            beginTime = DateUtil.offsetMinute(order.getPayTime(), 30);
        }
        User user = userService.findById(order.getUserId());
        String time =  DateUtil.format(beginTime, "HH:mm");
        String pushMessage = StrUtil.format("【触电】{}提示您尽快对“王者荣耀”进行接单，请快速处理(如已处理，请忽略)",user.getNickname());
        String orderMessage = StrUtil.format("请在{}前接单,否则订单取消",time);
        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(order.getServiceUserId())
                .title("订单消息")
                .alert(pushMessage)
                .build();
        pushMsg(appPushMsgVO);
        orderMsgService.createServerOrderMsg(order,orderMessage);
    }

    @Override
    public void remindStart(Order order) {
        //【触电】用户名称提示您立即对“王者荣耀”进行开始，请快速处理(如已处理，请忽略)
        Date beginTime = order.getBeginTime();
        if(beginTime==null){
            beginTime = DateUtil.offsetMinute(order.getReceivingTime(), 30);
        }
        User user = userService.findById(order.getUserId());
        String time =  DateUtil.format(beginTime, "HH:mm");
        String pushMessage = StrUtil.format("【触电】{}提示您立即对“王者荣耀”进行开始，请快速处理(如已处理，请忽略)",user.getNickname());
        String orderMessage = StrUtil.format("请在{}前开始服务,否则订单取消",time);
        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(order.getServiceUserId())
                .title("订单消息")
                .alert(pushMessage)
                .build();
        pushMsg(appPushMsgVO);
        orderMsgService.createServerOrderMsg(order,orderMessage);
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
//        String time =  DateUtil.format(order.get, "HH:mm");
        try {
            OrderStatusDetails orderStatusDetails = orderStatusDetailsService.findByOrderAndStatus(order.getOrderNo(),order.getStatus());
            String date =  DateUtil.format(DateUtil.offsetHour(orderStatusDetails.getTriggerTime(),24), "MM月dd日 HH:mm");
            String orderMessage = StrUtil.format("订单将在{}默认完成",date);
            orderMsgService.createServerOrderMsg(order,orderMessage);
        }catch (Exception e){
            log.error("推送消息错误",e);
        }


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

    /**
     * 用户下单和支付
     * @param order
     */
    @Override
    public void orderPay(Order order) {
        //【触电】用户名称申请在8月20日 12:30与您进行王者荣耀服务，请确认接单。有疑问请联系客服
        Date beginTime = order.getBeginTime();
        if(beginTime==null){
            beginTime = DateUtil.offsetMinute(order.getPayTime(), 30);
        }
        User user = userService.findById(order.getUserId());
        String date =  DateUtil.format(beginTime, "MM月dd日 HH:mm");
        String time =  DateUtil.format(beginTime, "HH:mm");
        String pushMessage = StrUtil.format("【触电】{}申请在{}与您进行王者荣耀服务，请确认接单。有疑问请联系客服",user.getNickname(), date);
        String orderMessage = StrUtil.format("请在{}前接单,否则订单取消",time);

        AppPushMsgVO appPushMsgVO = AppPushMsgVO.newBuilder(order.getServiceUserId())
                .title("订单消息")
                .alert(pushMessage)
                .build();
        pushMsg(appPushMsgVO);

        orderMsgService.createServerOrderMsg(order,orderMessage);

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
