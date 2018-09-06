package com.fulu.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class UserException extends BizException {

    private ExceptionCode exceptionCode;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode {
        USER_NOT_EXIST_EXCEPTION(20001, "用户不存在"),
        USER_BANNED_EXCEPTION(20000, "用户被封禁"),
        USER_MISMATCH_EXCEPTION(20002, "用户不匹配"),
        IllEGAL_MOBILE_EXCEPTION(20003, "手机号参数为空或不合法"),
        IllEGAL_IMID_EXCEPTION(20004, "imId参数为空或不合法"),
        TECH_AUTH_NOT_EXIST_EXCEPTION(20005, "认证技能记录不存在"),
        MAINPHOTO_NOT_EXIST_EXCEPTION(20006, "用户尚未上传主图"),
        USER_INFO_NULL_EXCEPTION(20007, "未查询到用户信息"),
        USERNAME_DUMPLICATE_EXCEPTION(20008, "用户名重复"),
        NAME_DUMPLICATE_EXCEPTION(20009, "姓名重复"),
        SESSION_KEY_DISABLE_EXCEPTION(21001, "sessionKey过期"),
        WX_PHONE_NOT_EXIST_EXCEPTION(21002, "未获取用户手机号"),
        LOCK_SELF_EXCEPTION(21003, "试图禁用正在登录管理员"),
        LOCK_DENY_EXCEPTION(21004, "已被禁用，无此权限"),
        NO_WECHATECO_EXCEPTION(21005, "没有匹配的微信生态类型"),
        BODY_NO_AUTH(21006, "用户未进行身份验证"),
        BODY_ALREADY_AUTH(21007, "用户已经认证了身份");
        
        private int code;
        private String msg;
    }

    public UserException(ExceptionCode exceptionCode) {
        super();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
    }
}
