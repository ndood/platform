package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.AccessLog;
import lombok.Data;


/**
 * 访问日志记录表
 *
 * @author shijiaoyun
 * @date 2018-08-30 11:24:56
 */
@Data
public class AccessLogVO  extends AccessLog {
    /** 来访者昵称 */
    private String fromUserNickname;
    /** 来访者头像url */
    private String fromUserHeadUrl;
    /** 被访者昵称 */
    private String toUserNickname;
    /** 被访者头像url */
    private String toUserHeadUrl;
}
