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
        ROOM_NOT_EXIST(50002, "没有房间设置信息修改权限!"),
        ROOM_MANAGER_NOT_MATCHING(50004, "房间马甲类型不匹配!"),
        ROOM_PASSWORD_ERROR(50005, "房间密码不匹配!"),
        ROOM_MIC_INDEX_ERROR(50006, "麦位错误!"),
        ROOM_MIC_INDEX_EXIST(50007, "请先下麦再上麦!"),
        ROOM_USER_QUIT_EXCEPTION(50007, "用户已经退出房间无法操作!"),
        ROOM_COLLECT_REPEAT(50008, "已经收藏过该房间!");


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
