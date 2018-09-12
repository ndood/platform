package com.fulu.game.core.entity;

import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WxMaTemplateMessageVO {

    private String toUser;

    private String templateId;

    private String page;

    private String formId;

    private List<WxMaTemplateMessage.Data> data;

    private Integer platform;

    private Integer pushId;


}
