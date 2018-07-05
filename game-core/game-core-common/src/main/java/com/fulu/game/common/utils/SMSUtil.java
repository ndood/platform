package com.fulu.game.common.utils;

import com.fulu.game.common.component.CloopenSmsComponent;
import com.fulu.game.common.enums.SMSTemplateEnum;
import com.xiaoleilu.hutool.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;


@Slf4j
public class SMSUtil {

    /**
     * 发送短信
     * @param mobile
     * @param templateEnum
     * @param data
     */
    public static Boolean sendSMS(String mobile, SMSTemplateEnum templateEnum,String ... data){
        CloopenSmsComponent cloopenSmsComponent = ApplicationContextRegister.getBean(CloopenSmsComponent.class);
        try {
            return cloopenSmsComponent.sendTemplateSMS(mobile,templateEnum.getType(),data);
        }catch (Exception e){
            log.error("发送短信异常",e);
            return false;
        }
    }

    /**
     * 发送短信验证码
     * @param mobile
     */
    public static String sendVerificationCode(String mobile){
        String code = RandomUtil.randomNumbers(4);
        if(sendSMS(mobile,SMSTemplateEnum.VERIFICATION_CODE,code)){
            return code;
        }
        return null;
    }

    /**
     * 发送接单提醒
     * @param mobile
     * @param orderName
     */
    public static Boolean sendOrderReceivingRemind(String mobile,String orderName){
        return sendSMS(mobile,SMSTemplateEnum.ORDER_RECEIVING_REMIND,orderName);
    }

    /**
     * 发送留言通知短信
     * @param mobile
     * @param msg
     * @param date
     * @return
     */
    public static Boolean sendLeaveInform(String mobile, String msg, String date){
        return sendSMS(mobile,SMSTemplateEnum.SENDLEAVE_INFORM,msg,date);
    }

}
