package com.fulu.game.common.utils;

import cn.hutool.core.util.RandomUtil;
import com.fulu.game.common.component.CloopenSmsComponent;
import com.fulu.game.common.enums.SMSTemplateEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


@Slf4j
public class SMSUtil {

    /**
     * 发送短信
     *
     * @param mobile
     * @param templateEnum
     * @param data
     */
    public static Boolean sendSMS(String mobile, SMSTemplateEnum templateEnum, String... data) {
        if (StringUtils.isEmpty(mobile)) {
            log.error("发送短信手机号不能为空");
            return false;
        }
        if (templateEnum == null) {
            log.error("发送模板对象不能为空");
            return false;
        }
        CloopenSmsComponent cloopenSmsComponent = ApplicationContextRegister.getBean(CloopenSmsComponent.class);
        try {
            return cloopenSmsComponent.sendTemplateSMS(mobile, templateEnum.getType(), data);
        } catch (Exception e) {
            log.error("发送短信异常", e);
            return false;
        }
    }

    /**
     * 发送短信验证码
     *
     * @param mobile
     */
    public static String sendVerificationCode(String mobile) {
        String code = RandomUtil.randomNumbers(4);
        if (sendSMS(mobile, SMSTemplateEnum.VERIFICATION_CODE, code)) {
            return code;
        }
        return null;
    }

    /**
     * 发送接单提醒
     *
     * @param mobile
     * @param orderName
     */
    public static Boolean sendOrderReceivingRemind(String mobile, String orderName) {
        return sendSMS(mobile, SMSTemplateEnum.ORDER_RECEIVING_REMIND, orderName);
    }

    /**
     * 发送留言通知短信
     *
     * @param mobile
     * @param msg
     * @return
     */
    public static Boolean sendLeaveInform(String mobile, String msg, String url) {
        return sendSMS(mobile, SMSTemplateEnum.SENDLEAVE_INFORM, msg, url);
    }

    /**
     * 发送浏览通知短信（不带url）
     *
     * @param mobile 手机号码
     * @param msg    留言内容
     * @return
     */
    public static Boolean sendLeaveInformNoUrl(String mobile, String msg) {
        return sendSMS(mobile, SMSTemplateEnum.SENDLEAVE_INFORM_NO_URL, msg);
    }
}
