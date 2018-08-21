package com.fulu.game.core.service.impl.push;

import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.OrderTypeEnum;
import com.fulu.game.common.enums.WechatEcoEnum;
import com.fulu.game.common.enums.WechatTemplateIdEnum;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.common.utils.SMSUtil;
import com.fulu.game.core.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AdminPushServiceImpl extends PushServiceImpl {


    /**
     * 客服仲裁，用户胜
     *
     * @param order
     */
    public void appealUserWin(Order order) {
        if (OrderTypeEnum.PLATFORM.getType().equals(order.getType())) {
            pushWechatTemplateMsg(WechatEcoEnum.PLAY.getType(), order.getUserId(), WechatTemplateIdEnum.PLAY_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_USER_WIN.getPage().getPlayPagePath(), WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_USER_WIN.getContent());
            pushWechatTemplateMsg(WechatEcoEnum.PLAY.getType(), order.getServiceUserId(), WechatTemplateIdEnum.PLAY_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_USER_WIN.getPage().getPlayPagePath(), WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_USER_WIN.getContent());
        } else if(OrderTypeEnum.POINT.getType().equals(order.getType())){
            pushWechatTemplateMsg(WechatEcoEnum.POINT.getType(), order.getUserId(), WechatTemplateIdEnum.POINT_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_USER_WIN.getPage().getPointPagePath(), WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_USER_WIN.getContent());
            pushWechatTemplateMsg(WechatEcoEnum.POINT.getType(), order.getServiceUserId(), WechatTemplateIdEnum.POINT_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL_USER_WIN.getPage().getPointPagePath(), WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_USER_WIN.getContent());
        }else if(OrderTypeEnum.H5.getType().equals(order.getType())){
            pushWechatTemplateMsg(WechatEcoEnum.POINT.getType(), order.getServiceUserId(), WechatTemplateIdEnum.POINT_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL_USER_WIN.getPage().getPointPagePath(), WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_USER_WIN.getContent());
        }
    }

    /**
     * 客服仲裁，陪玩师胜
     *
     * @param order
     */
    public void appealServiceWin(Order order) {
        if (OrderTypeEnum.PLATFORM.getType().equals(order.getType())) {
            pushWechatTemplateMsg(WechatEcoEnum.PLAY.getType(), order.getUserId(), WechatTemplateIdEnum.PLAY_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_SERVICE_WIN.getPage().getPlayPagePath(), WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_SERVICE_WIN.getContent());
            pushWechatTemplateMsg(WechatEcoEnum.PLAY.getType(), order.getServiceUserId(), WechatTemplateIdEnum.PLAY_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL_SERVICE_WIN.getPage().getPlayPagePath(), WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL_SERVICE_WIN.getContent());
        } else if(OrderTypeEnum.POINT.getType().equals(order.getType())){
            pushWechatTemplateMsg(WechatEcoEnum.POINT.getType(), order.getUserId(), WechatTemplateIdEnum.POINT_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_SERVICE_WIN.getPage().getPointPagePath(), WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_SERVICE_WIN.getContent());
            pushWechatTemplateMsg(WechatEcoEnum.POINT.getType(), order.getServiceUserId(), WechatTemplateIdEnum.POINT_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL_SERVICE_WIN.getPage().getPointPagePath(), WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_SERVICE_WIN.getContent());
        } else if(OrderTypeEnum.H5.getType().equals(order.getType())){
            //todo 给用户发送仲裁短信
            pushWechatTemplateMsg(WechatEcoEnum.POINT.getType(), order.getServiceUserId(), WechatTemplateIdEnum.POINT_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL_SERVICE_WIN.getPage().getPointPagePath(), WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_SERVICE_WIN.getContent());

        }
    }

    /**
     * 客服仲裁协商处理
     *
     * @param order
     * @param msg
     */
    public void appealNegotiate(Order order, String msg) {
        if (OrderTypeEnum.PLATFORM.getType().equals(order.getType())) {
            pushWechatTemplateMsg(WechatEcoEnum.PLAY.getType(), order.getUserId(), WechatTemplateIdEnum.PLAY_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE.getPage().getPlayPagePath(), WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE.getContent(), msg);
            pushWechatTemplateMsg(WechatEcoEnum.PLAY.getType(), order.getServiceUserId(), WechatTemplateIdEnum.PLAY_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE.getPage().getPlayPagePath(), WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE.getContent(), msg);
        } else if(OrderTypeEnum.POINT.getType().equals(order.getType())){
            pushWechatTemplateMsg(WechatEcoEnum.POINT.getType(), order.getUserId(), WechatTemplateIdEnum.POINT_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE.getPage().getPointPagePath(), WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE.getContent(), msg);
            pushWechatTemplateMsg(WechatEcoEnum.POINT.getType(), order.getServiceUserId(), WechatTemplateIdEnum.POINT_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE.getPage().getPointPagePath(), WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE.getContent(), msg);
        }else if(OrderTypeEnum.H5.getType().equals(order.getType())){
            //todo 给用户发送仲裁短信
            pushWechatTemplateMsg(WechatEcoEnum.POINT.getType(), order.getServiceUserId(), WechatTemplateIdEnum.POINT_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE.getPage().getPointPagePath(), WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE.getContent(), msg);
        }
    }

    /**
     * 发放优惠券
     *
     * @param userId
     * @param deduction
     */
    public void grantCouponMsg(int userId, String deduction) {
        pushWechatTemplateMsg(WechatEcoEnum.PLAY.getType(), userId, WechatTemplateIdEnum.PLAY_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_TOSERVICE_AFFIRM_SERVER.getPage().getPlayPagePath(), WechatTemplateMsgEnum.ORDER_TOSERVICE_AFFIRM_SERVER.getContent(), deduction);
    }

    /**
     * 陪玩师技能审核通过
     */
    public void techAuthAuditSuccess(Integer userId) {
        pushWechatTemplateMsg(WechatEcoEnum.PLAY.getType(), userId, WechatTemplateIdEnum.PLAY_LEAVE_MSG, WechatTemplateMsgEnum.TECH_AUTH_AUDIT_SUCCESS.getPage().getPlayPagePath(), WechatTemplateMsgEnum.TECH_AUTH_AUDIT_SUCCESS.getContent());
    }

    /**
     * 陪玩师技能审核通过
     */
    public void techAuthAuditFail(Integer userId, String msg) {
        pushWechatTemplateMsg(WechatEcoEnum.PLAY.getType(), userId, WechatTemplateIdEnum.PLAY_LEAVE_MSG, WechatTemplateMsgEnum.TECH_AUTH_AUDIT_FAIL.getPage().getPlayPagePath(), WechatTemplateMsgEnum.TECH_AUTH_AUDIT_FAIL.getContent(), msg);
    }

    /**
     * 陪玩师个人信息审核不通过
     */
    public void userInfoAuthFail(Integer userId, String msg) {
        pushWechatTemplateMsg(WechatEcoEnum.PLAY.getType(), userId, WechatTemplateIdEnum.PLAY_LEAVE_MSG, WechatTemplateMsgEnum.USER_AUTH_INFO_REJECT.getPage().getPlayPagePath(), WechatTemplateMsgEnum.USER_AUTH_INFO_REJECT.getContent(), msg);
    }

    /**
     * 陪玩师个人信息审核通过
     *
     * @param userId
     */
    public void userInfoAuthSuccess(Integer userId) {
        pushWechatTemplateMsg(WechatEcoEnum.PLAY.getType(), userId, WechatTemplateIdEnum.PLAY_LEAVE_MSG, WechatTemplateMsgEnum.USER_AUTH_INFO_PASS.getPage().getPlayPagePath(), WechatTemplateMsgEnum.USER_AUTH_INFO_PASS.getContent());
    }


    public void adminPushWxTemplateMsg(int platform, int pushId, List<Integer> userIds, String page, String cotent) {
        String date = DateUtil.format(new Date(), "yyyy年MM月dd日 HH:mm");
        List<WxMaTemplateMessage.Data> dataList = CollectionUtil.newArrayList(
                new WxMaTemplateMessage.Data("keyword1", cotent),
                new WxMaTemplateMessage.Data("keyword2", date));
        if (WechatEcoEnum.PLAY.getType().equals(platform)) {
            addTemplateMsg2Queue(platform, pushId, userIds, page, WechatTemplateIdEnum.PLAY_LEAVE_MSG, dataList);
        } else if (WechatEcoEnum.POINT.getType().equals(platform)) {
            addTemplateMsg2Queue(platform, pushId, userIds, page, WechatTemplateIdEnum.POINT_LEAVE_MSG, dataList);
        }
    }

}
