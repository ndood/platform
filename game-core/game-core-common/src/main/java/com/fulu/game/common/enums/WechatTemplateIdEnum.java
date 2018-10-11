package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
public enum WechatTemplateIdEnum {


    PLAY_LEAVE_MSG("yD7JulFzNv7ZNInswmn6_hdgwlf68qRL0fwLUNq98Vc", "留言消息模板", "keyword1，消息时间：keyword2",
            SMSTemplateEnum.SMS_REMIND),
    POINT_LEAVE_MSG("q4vzN44BKXw-ZDLH-g7qsPgFb6UEj0enkutCwNfAuh0", "留言消息模板", "keyword1，消息时间：keyword2",
            SMSTemplateEnum.SMS_REMIND),
    PLAY_SERVICE_PROCESS_NOTICE("tXFoUhfqrMRADhrF4Sp1yaiUwpw6H6XwBc_xf1LJzo8", "服务进度通知模板",
            "keyword1，订单编号：keyword2，订单金额：keyword3，通知时间：keyword4，服务人员：" +
            "keyword5，备注：keyword6", SMSTemplateEnum.SMS_REMIND),
    POINT_SERVICE_PROCESS_NOTICE("pO3Wjr5bbtiublql1uV4gfdEypWO7g3z5_zFdLa8-Wg", "服务进度通知模板",
            "keyword1，订单编号：keyword2，订单金额：keyword3，通知时间：keyword4，服务人员：" +
            "keyword5，备注：keyword6", SMSTemplateEnum.SMS_REMIND);

    private String templateId;
    private String msg;
    //用于微信模板消息转化为短信模板消息要填充到短信模板消息的内容
    private String content;
    //要转化的短信模板
    private SMSTemplateEnum smsTemplate;

    public static WechatTemplateIdEnum getWxTemplate(String templateId){
        for(WechatTemplateIdEnum wechatTemplateIdEnum:WechatTemplateIdEnum.values()){
            if(StringUtils.equals(wechatTemplateIdEnum.getTemplateId(), templateId)){
                return wechatTemplateIdEnum;
            }
        }
        return null;
    }
}
