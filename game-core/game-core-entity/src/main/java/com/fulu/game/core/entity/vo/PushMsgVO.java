package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.PushMsg;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 *
 * @author wangbin
 * @date 2018-06-06 10:29:12
 */
@Data
public class PushMsgVO  extends PushMsg {


    //推送类型
    @NotNull(message = "[推送类型]字段不能为空")
    private Integer type;
    @NotNull(message = "[平台类型]字段不能为空")
    private Integer platform;
    //落地页
    @NotNull(message = "[落地页]字段不能为空")
    private String page;
    //推送ID
    private String pushIds;
    //推送内容
    @NotNull(message = "[推送内容]字段不能为空")
    private String content;
    //触发时间
    private Date touchTime;


}
