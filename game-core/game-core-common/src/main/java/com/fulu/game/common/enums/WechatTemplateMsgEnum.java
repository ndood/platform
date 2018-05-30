package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WechatTemplateMsgEnum {

    TECH_AUTH_AUDIT_ING("pages/a/card/card", WechatTemplateEnum.PUSH_MSG.getType(), "您的好友{}成功帮您认证,还剩{}位好友认证即可成功通过审核."), //好友认证
    TECH_AUTH_AUDIT_SUCCESS("pages/a/setting/setting", WechatTemplateEnum.PUSH_MSG.getType(), "恭喜您,成功通过审核,快快前往接单啦。"),
    TECH_AUTH_AUDIT_FAIL("pages/a/info/info", WechatTemplateEnum.PUSH_MSG.getType(), "审核未通过:{},请前往重新提交审核."),

    GRANT_COUPON("优惠券页", WechatTemplateEnum.PUSH_MSG.getType(), "{},恭喜你,获得一张{}元优惠券");

    //跳转页面
    private String page;
    //模板代码
    private String templateId;
    //内容
    private String content;


}
