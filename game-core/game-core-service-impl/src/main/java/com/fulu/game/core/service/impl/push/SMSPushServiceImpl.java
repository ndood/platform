package com.fulu.game.core.service.impl.push;

import cn.hutool.core.util.StrUtil;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.SMSTemplateEnum;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.PushMsg;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.SMSVO;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.queue.SMSPushContainer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


@Service
@Slf4j
public class SMSPushServiceImpl extends PushServiceImpl {

    @Autowired
    private SMSPushContainer smsPushContainer;
    @Autowired
    private UserService userService;

    private static final int PAGESIZE = 100;

    /**
     * 执行推送方法
     *
     * @param smsvo
     */
    public void pushMsg(SMSVO smsvo) {
        smsPushContainer.add(smsvo);
    }

    /**
     * 陪玩师开始接单，跟用户发短信
     *
     * @param order
     */
    @Override
    public void receiveOrder(Order order) {
        User user = userService.findById(order.getUserId());
        String content = WechatTemplateMsgEnum.ORDER_TOUSER_AFFIRM_RECEIVE.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);

    }

    @Override
    public void remindReceive(Order order) {
        User user = userService.findById(order.getServiceUserId());
        String content = WechatTemplateMsgEnum.ORDER_TOSERVICE_REMIND_RECEIVE.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }

    @Override
    public void remindStart(Order order) {
        User user = userService.findById(order.getUserId());
        String content = WechatTemplateMsgEnum.ORDER_TOSERVICE_REMIND_START_SERVICE.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }


    @Override
    public void serviceUserAcceptOrder(Order order) {
        User user = userService.findById(order.getServiceUserId());
        String content = WechatTemplateMsgEnum.POINT_TOSERVICE_ORDER_RECEIVING.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }

    @Override
    public void start(Order order) {
        User user = userService.findById(order.getUserId());
        String content = WechatTemplateMsgEnum.ORDER_TOUSER_START_SERVICE.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }

    @Override
    public void consult(Order order) {
        User user = userService.findById(order.getServiceUserId());
        String content = WechatTemplateMsgEnum.ORDER_TOSERVICE_CONSULT.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }

    @Override
    public void rejectConsult(Order order) {
        User user = userService.findById(order.getUserId());
        String content = WechatTemplateMsgEnum.ORDER_TOUSER_CONSULT_REJECT.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }

    @Override
    public void agreeConsult(Order order) {
        User user = userService.findById(order.getUserId());
        String content = WechatTemplateMsgEnum.ORDER_TOUSER_CONSULT_AGREE.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }

    @Override
    public void cancelConsult(Order order) {
        User user = userService.findById(order.getServiceUserId());
        String content = WechatTemplateMsgEnum.ORDER_TOSERVICE_CONSULT_CANCEL.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }

    @Override
    public void cancelOrderByServer(Order order) {
        User user = userService.findById(order.getUserId());
        String content = WechatTemplateMsgEnum.ORDER_TOUSER_REJECT_RECEIVE.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }

    @Override
    public void cancelOrderByUser(Order order) {
        User user = userService.findById(order.getServiceUserId());
        String content = WechatTemplateMsgEnum.ORDER_TOSERVICE_ORDER_CANCEL.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }

    @Override
    public void appealByServer(Order order) {
        User user = userService.findById(order.getUserId());
        String content = WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }

    @Override
    public void appealByUser(Order order) {
        User user = userService.findById(order.getServiceUserId());
        String content = WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }

    @Override
    public void checkOrder(Order order) {
        User user = userService.findById(order.getUserId());
        String content = WechatTemplateMsgEnum.ORDER_TOUSER_CHECK.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }

    /**
     * 用户下单和支付
     *
     * @param order
     */
    @Override
    public void orderPay(Order order) {
        User user = userService.findById(order.getServiceUserId());
        String content = WechatTemplateMsgEnum.ORDER_TOSERVICE_PAY.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }

    @Override
    public void acceptOrder(Order order) {
        User user = userService.findById(order.getServiceUserId());
        String content = WechatTemplateMsgEnum.ORDER_TOSERVICE_AFFIRM_SERVER.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }

    /**
     * 后台推送，暂不考虑短信
     *
     * @param pushMsg
     * @param userIds
     * @param page
     */
    @Override
    public void adminPush(PushMsg pushMsg, List<Integer> userIds, String page) {
        int pagenum = 0;
        String content = pushMsg.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        List<User> users;
        SMSVO smsvo;
        do {
            users = userService.findByUserIds(userIds, Boolean.TRUE, pagenum, PAGESIZE);
            for (User user : users) {
                smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
                pushMsg(smsvo);
            }
        } while (CollectionUtils.isEmpty(users));
    }

    /**
     * 客服仲裁，用户胜
     *
     * @param order
     */
    @Override
    public void appealUserWin(Order order) {
        User user = userService.findById(order.getUserId());
        String content = WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_USER_WIN.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);

        User sUser = userService.findById(order.getServiceUserId());
        String sContent = WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL_USER_WIN.getContent();
        String[] sParams = {sContent, Constant.WEIXN_JUMP_URL};
        SMSVO sSmsvo = new SMSVO(sUser.getMobile(), SMSTemplateEnum.SMS_REMIND, sParams);
        pushMsg(sSmsvo);
    }

    /**
     * 客服仲裁，陪玩师胜
     *
     * @param order
     */
    @Override
    public void appealServiceWin(Order order) {
        User user = userService.findById(order.getUserId());
        String content = WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_SERVICE_WIN.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);

        User sUser = userService.findById(order.getServiceUserId());
        String sContent = WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL_SERVICE_WIN.getContent();
        String[] sParams = {sContent, Constant.WEIXN_JUMP_URL};
        SMSVO sSmsvo = new SMSVO(sUser.getMobile(), SMSTemplateEnum.SMS_REMIND, sParams);
        pushMsg(sSmsvo);
    }

    /**
     * 客服仲裁协商处理
     *
     * @param order
     * @param msg
     */
    @Override
    public void appealNegotiate(Order order, String msg) {
        User user = userService.findById(order.getUserId());
        String content = WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);

        User sUser = userService.findById(order.getServiceUserId());
        SMSVO sSmsvo = new SMSVO(sUser.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(sSmsvo);
    }

    /**
     * 发放优惠券
     *
     * @param userId
     * @param deduction
     */
    @Override
    public void grantCouponMsg(int userId, String deduction) {
        User user = userService.findById(userId);
        String content = WechatTemplateMsgEnum.GRANT_COUPON.getContent();
        if (StringUtils.isNotEmpty(deduction)) {
            content = StrUtil.format(content, deduction);
        }
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }

    /**
     * 陪玩师技能审核通过
     */
    @Override
    public void techAuthAuditSuccess(Integer userId) {
        User user = userService.findById(userId);
        String content = WechatTemplateMsgEnum.TECH_AUTH_AUDIT_SUCCESS.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }

    /**
     * 陪玩师技能审核不通过
     */
    @Override
    public void techAuthAuditFail(Integer userId, String msg) {
        User user = userService.findById(userId);
        String content = WechatTemplateMsgEnum.TECH_AUTH_AUDIT_FAIL.getContent();
        if (StringUtils.isNotEmpty(msg)) {
            content = StrUtil.format(content, msg);
        }
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }

    /**
     * 陪玩师个人信息审核不通过
     */
    @Override
    public void userInfoAuthFail(Integer userId, String msg) {
        User user = userService.findById(userId);
        String content = WechatTemplateMsgEnum.USER_AUTH_INFO_REJECT.getContent();
        if (StringUtils.isNotEmpty(msg)) {
            content = StrUtil.format(content, msg);
        }
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }

    /**
     * 陪玩师个人信息审核通过
     *
     * @param userId
     */
    @Override
    public void userInfoAuthSuccess(Integer userId) {
        User user = userService.findById(userId);
        String content = WechatTemplateMsgEnum.USER_AUTH_INFO_PASS.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }

    /**
     * 同意协商
     *
     * @param order
     */
    public void consultAgree(Order order) {
        agreeConsult(order);
    }

    /**
     * 取消协商
     *
     * @param order
     */
    public void consultCancel(Order order) {
        User user = userService.findById(order.getUserId());
        String content = WechatTemplateMsgEnum.ORDER_TOSERVICE_CONSULT_CANCEL.getContent();
        String[] params = {content, Constant.WEIXN_JUMP_URL};
        SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
        pushMsg(smsvo);
    }

}
