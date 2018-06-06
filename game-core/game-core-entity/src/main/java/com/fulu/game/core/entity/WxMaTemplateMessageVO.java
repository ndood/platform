package com.fulu.game.core.entity;

import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WxMaTemplateMessageVO {

    private Integer pushId;

    private WxMaTemplateMessage wxMaTemplateMessage;

}
