package com.fulu.game.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class UploadException  extends RuntimeException{

    private ExceptionCode exceptionCode;
    private Integer code;
    private String message;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode{
        URL_PATH_ERROR(1002, "url路径错误!");
        private int code;
        private String msg;
    }


    public UploadException(ExceptionCode exceptionCode) {
        super();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
    }

}
