package com.fulu.game.core.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.WechatTemplateEnum;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.WechatFormid;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.WechatFormidService;
import com.fulu.game.core.service.WxTemplateMsgService;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class WxTemplateMsgServiceImpl implements WxTemplateMsgService{

    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private UserService userService;
    @Autowired
    private WxMaService wxMaService;
    @Autowired
    private WechatFormidService wechatFormidService;

    private static final int LOCK_NUM = 10000;
    private List<Object> objects = new ArrayList<>(LOCK_NUM);
    {
        for(Integer i=0;i<LOCK_NUM;i++){
            objects.add(i);
        }
    }

    @Override
    public String pushWechatTemplateMsg(String content,
                                        String imId){
        if(redisOpenService.hasKey(RedisKeyEnum.WX_TEMPLATE_MSG.generateKey(imId))){
            return "消息已经推送过了!";
        }
        User user = userService.findByImId(imId);
        if(user==null||user.getOpenId()==null){
            throw new ServiceErrorException("IM不存在!");
        }
        String formId = getWechatUserFormId(user.getId());
        if(formId==null){
            throw new ServiceErrorException("无法给该用户推送消息!");
        }
        String date = DateUtil.format(new Date(),"yyyy年MM月dd日 HH:mm");
        WxMaTemplateMessage wxMaTemplateMessage = new WxMaTemplateMessage();
        wxMaTemplateMessage.setTemplateId(WechatTemplateEnum.PUSH_MSG.getType());
        wxMaTemplateMessage.setToUser(user.getOpenId());
        wxMaTemplateMessage.setPage("/pages/index/index");
        wxMaTemplateMessage.setFormId(formId);
        List<WxMaTemplateMessage.Data> dataList = CollectionUtil.newArrayList(new WxMaTemplateMessage.Data("keyword1", user.getNickname()+":"+content),new WxMaTemplateMessage.Data("keyword2", date));
        wxMaTemplateMessage.setData(dataList);
        try {
            wxMaService.getMsgService().sendTemplateMsg(wxMaTemplateMessage);
        }catch (Exception e){
            throw new ServiceErrorException("推送消息出错!");
        }
        //推送状态缓存两个小时
        redisOpenService.set(RedisKeyEnum.WX_TEMPLATE_MSG.generateKey(imId),imId, Constant.TIME_HOUR_TOW);
        return "消息推送成功!";
    }


    private String getWechatUserFormId(Integer userId) {
        List<WechatFormid> formidList = wechatFormidService.findInSevenDaysFormIdByUser(userId);
        if (formidList.isEmpty()) {
            return null;
        }
        synchronized (objects.get(userId % LOCK_NUM)) {
            WechatFormid formid = formidList.get(0);
            try {
                return formid.getFormId();
            } finally {
                wechatFormidService.deleteNotAvailableFormIds(formid);
            }
        }
    }



}
