package com.fulu.game.core.service.impl.push;

import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.enums.WechatTemplateIdEnum;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.PushMsg;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class PlayMiniAppPushServiceImpl extends MiniAppPushServiceImpl {

    @Autowired
    private UserService userService;

    /**
     * 微信模板消息推送
     * 现在微信模板消息只有两个模板：
     * 一种是留言通知，比如审核通过，im收到消息等
     *
     * @param userIds
     * @param wechatTemplateMsgEnum
     * @param replaces
     */
    @Override
    protected void push(List<Integer> userIds, WechatTemplateMsgEnum wechatTemplateMsgEnum, String... replaces) {
        push(null, userIds, wechatTemplateMsgEnum, replaces);
    }

    protected void push(Integer pushId, List<Integer> userIds, WechatTemplateMsgEnum wechatTemplateMsgEnum, String... replaces) {
        WechatTemplateIdEnum templateId = WechatTemplateIdEnum.PLAY_LEAVE_MSG;
        String page = wechatTemplateMsgEnum.getPage().getPlayPagePath();
        String content = wechatTemplateMsgEnum.getContent();
        if (replaces != null && replaces.length > 0) {
            content = StrUtil.format(content, replaces);
        }
        String date = DateUtil.format(new Date(), "yyyy年MM月dd日 HH:mm");
        List<WxMaTemplateMessage.Data> dataList = CollectionUtil.newArrayList(new WxMaTemplateMessage.Data("keyword1", content),
                new WxMaTemplateMessage.Data("keyword2", date));

        addTemplateMsg2Queue(PlatformEcoEnum.PLAY.getType(), pushId, userIds, page, templateId, dataList);
    }

    /**
     * 微信模板消息推送
     * 现在微信模板消息只有两个模板：
     * 另一种是“服务进度通知”，比如订单流程中，已下单，已支付等状态变更都会有消息通知
     * 这两种目前区别在于是否跟订单相关
     *
     * @param userIds
     * @param order
     * @param wechatTemplateMsgEnum
     * @param replaces
     */
    @Override
    protected void push(List<Integer> userIds, Order order, WechatTemplateMsgEnum wechatTemplateMsgEnum, String... replaces) {
        WechatTemplateIdEnum templateId = WechatTemplateIdEnum.PLAY_SERVICE_PROCESS_NOTICE;
        String content = wechatTemplateMsgEnum.getContent();
        if (replaces != null && replaces.length > 0) {
            content = StrUtil.format(content, replaces);
        }
        String date = DateUtil.format(DateUtil.date(), "yyyy年MM月dd日 HH:mm");
        List<WxMaTemplateMessage.Data> dataList;
        String serviceUserNickName = "";
        User serviceUser = userService.findById(order.getServiceUserId());
        if (serviceUser != null) {
            serviceUserNickName = serviceUser.getNickname();
        }

        dataList = CollectionUtil.newArrayList(
                //服务进度（订单状态）
                new WxMaTemplateMessage.Data("keyword1", OrderStatusEnum.getMsgByStatus(order.getStatus())),
                //订单编号
                new WxMaTemplateMessage.Data("keyword2", order.getOrderNo()),
                //订单金额
                new WxMaTemplateMessage.Data("keyword3", "￥" + order.getTotalMoney().toPlainString()),
                //通知时间
                new WxMaTemplateMessage.Data("keyword4", date),
                //服务人员（陪玩师姓名）
                new WxMaTemplateMessage.Data("keyword5", serviceUserNickName),
                //备注（消息模板内容）
                new WxMaTemplateMessage.Data("keyword6", content));
        addTemplateMsg2Queue(PlatformEcoEnum.PLAY.getType(), null, userIds,
                wechatTemplateMsgEnum.getPage().getPlayPagePath(), templateId, dataList);
    }

    @Override
    public void adminPush(PushMsg pushMsg, List<Integer> userIds, String page) {
        String date = DateUtil.format(new Date(), "yyyy年MM月dd日 HH:mm");
        List<WxMaTemplateMessage.Data> dataList = CollectionUtil.newArrayList(
                new WxMaTemplateMessage.Data("keyword1", pushMsg.getContent()),
                new WxMaTemplateMessage.Data("keyword2", date));
        addTemplateMsg2Queue(pushMsg.getPlatform(), pushMsg.getId(), userIds, page, WechatTemplateIdEnum.PLAY_LEAVE_MSG, dataList);
    }


    /**
     * 客服仲裁，用户胜
     *
     * @param order
     */
    @Override
    public void appealUserWin(Order order) {
        List<Integer> userIds = Collections.singletonList(order.getUserId());
        push(userIds, order, WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_USER_WIN);
        push(userIds, order, WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL_USER_WIN);
    }

    /**
     * 客服仲裁，陪玩师胜
     *
     * @param order
     */
    @Override
    public void appealServiceWin(Order order) {
        List<Integer> userIds = Collections.singletonList(order.getUserId());
        push(userIds, order, WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_SERVICE_WIN);
        push(userIds, order, WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL_SERVICE_WIN);
    }

    /**
     * 客服仲裁协商处理
     *
     * @param order
     * @param msg
     */
    @Override
    public void appealNegotiate(Order order, String msg) {
        List<Integer> userIds = Collections.singletonList(order.getUserId());
        push(userIds, order, WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE, msg);
        push(userIds, order, WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE, msg);
    }

    /**
     * 发放优惠券
     *
     * @param userId
     * @param deduction
     */
    @Override
    public void grantCouponMsg(int userId, String deduction) {
        List<Integer> userIds = Collections.singletonList(userId);
        push(userIds, WechatTemplateMsgEnum.GRANT_COUPON, deduction);
    }

    /**
     * 陪玩师技能审核通过
     */
    @Override
    public void techAuthAuditSuccess(Integer userId) {
        List<Integer> userIds = Collections.singletonList(userId);
        push(userIds, WechatTemplateMsgEnum.TECH_AUTH_AUDIT_SUCCESS);
    }

    /**
     * 陪玩师技能审核通过
     */
    @Override
    public void techAuthAuditFail(Integer userId, String msg) {
        List<Integer> userIds = Collections.singletonList(userId);
        push(userIds, WechatTemplateMsgEnum.TECH_AUTH_AUDIT_FAIL, msg);
    }

    /**
     * 陪玩师个人信息审核不通过
     */
    @Override
    public void userInfoAuthFail(Integer userId, String msg) {
        List<Integer> userIds = Collections.singletonList(userId);
        push(userIds, WechatTemplateMsgEnum.USER_AUTH_INFO_REJECT, msg);
    }

    /**
     * 陪玩师个人信息审核通过
     *
     * @param userId
     */
    @Override
    public void userInfoAuthSuccess(Integer userId) {
        List<Integer> userIds = Collections.singletonList(userId);
        push(userIds, WechatTemplateMsgEnum.USER_AUTH_INFO_PASS);
    }
}
