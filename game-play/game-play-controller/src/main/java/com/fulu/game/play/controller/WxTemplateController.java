package com.fulu.game.play.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.WechatTemplateEnum;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/msg")
public class WxTemplateController {

    @Autowired
    private UserService userService;
    @Autowired
    private WxMaService wxMaService;


    @RequestMapping("/push")
    public Result pushWechatMsg(String formId,
                                String content,
                                String imId)throws Exception{
        log.info("推送模板消息formId:{},imId:{},content:{}",formId,imId,content);
        User user = userService.findByImId(imId);
        if(user==null||user.getOpenId()==null){
            return Result.error().msg("IM不存在!");
        }
        String date = DateUtil.format(new Date(),"yyyy年MM月dd日 HH:mm");
        WxMaTemplateMessage wxMaTemplateMessage = new WxMaTemplateMessage();
        wxMaTemplateMessage.setTemplateId(WechatTemplateEnum.PUSH_MSG.getType());
        wxMaTemplateMessage.setToUser(user.getOpenId());
        wxMaTemplateMessage.setFormId(formId);
        List<WxMaTemplateMessage.Data> dataList =CollectionUtil.newArrayList(new WxMaTemplateMessage.Data("keyword1", user.getNickname()+":"+content),new WxMaTemplateMessage.Data("keyword2", date));
        wxMaTemplateMessage.setData(dataList);
        wxMaService.getMsgService().sendTemplateMsg(wxMaTemplateMessage);
        return Result.success().msg("消息发送成功!");
    }









}
