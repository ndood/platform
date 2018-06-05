package com.fulu.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class SearchException extends BizException{


    private ExceptionCode exceptionCode;
    private String sql;
    private String errorMsg;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode{
        FIND_EXCEPTION(100, "查询索引错误"),
        SAVE_EXCEPTION(101, "新建索引错误"),
        DEL_EXCEPTION(103, "删除索引错误");
        private int code;
        private String msg;
    }

    public SearchException(ExceptionCode exceptionCode,String sql,String errorMsg) {
        super();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
        this.sql = sql;
        this.errorMsg =errorMsg;
    }

    @Override
    public String getMessage() {
        return super.getMessage()+";\n sql:"+sql+";\nerrorMsg:"+errorMsg;
    }
}
