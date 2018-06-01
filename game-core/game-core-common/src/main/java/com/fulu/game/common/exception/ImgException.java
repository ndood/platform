package com.fulu.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ImgException extends BizException {
    private ExceptionCode exceptionCode;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode {
        SHARE_NOT_EXSISTS(80001, "未配置该分享文案"),
        SHARECONTENT_BLANK(80002, "分享文案内容配置为空"),
        JSONFORMAT_ERROR(80003, "技能认证分享文案内容格式错误"),
        CATEGORY_ICON_URL(80004, "游戏图标未配置");
        private int code;
        private String msg;
    }

    public ImgException(ExceptionCode exceptionCode) {
        super();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
    }
}
