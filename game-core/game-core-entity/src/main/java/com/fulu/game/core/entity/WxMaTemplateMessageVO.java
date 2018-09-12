package com.fulu.game.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WxMaTemplateMessageVO {

    private String toUser;

    private String templateId;

    private String page;

    private String formId;

//    private List<WxMaTemplateMessage.Data> data;

    private String dataJson;

    private Integer platform;

    private Integer pushId;


}
