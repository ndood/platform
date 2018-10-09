package com.fulu.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class RoomException extends BizException {

    private ExceptionCode exceptionCode;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode {
        ROOM_UPDATE_NO_PERMISSIONS(50001, "没有房间设置信息修改权限!"),
        ROOM_NOT_EXIST(50002, "没有房间设置信息修改权限!");


        private int code;
        private String msg;
    }

    public RoomException(ExceptionCode exceptionCode) {
        super();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
    }
}
