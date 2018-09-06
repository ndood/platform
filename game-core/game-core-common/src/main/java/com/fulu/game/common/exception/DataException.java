package com.fulu.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据异常 例如表单token验证不通过等
 */
@Getter
public class DataException extends RuntimeException{

    private ExceptionCode exceptionCode;

    private Integer code;
    private String message;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode{
        NO_FORM_TOKEN_ERROR(4001, "页面超时,需要重新刷新页面后再尝试!"); //表单token验证不通过
        private int code;
        private String msg;
    }

    public DataException(ExceptionCode exceptionCode) {
        super();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
    }
}
