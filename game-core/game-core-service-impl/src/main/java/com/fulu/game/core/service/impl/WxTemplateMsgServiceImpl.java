package com.fulu.game.core.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.WechatTemplateEnum;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.WechatFormid;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.WechatFormidService;
import com.fulu.game.core.service.WxTemplateMsgService;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class WxTemplateMsgServiceImpl implements WxTemplateMsgService {

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
        for (Integer i = 0; i < LOCK_NUM; i++) {
            objects.add(i);
        }
    }


    public Boolean pushWechatTemplateMsg(Integer userId,
                                         WechatTemplateMsgEnum wechatTemplateMsgEnum,
                                         String... replaces) {
        User user = userService.findById(userId);
        String content = wechatTemplateMsgEnum.getContent();
        if (replaces != null && replaces.length > 0) {
            content = StrUtil.format(content, replaces);
        }
        return pushWechatTemplateMsg(user.getId(), wechatTemplateMsgEnum.getPage(), wechatTemplateMsgEnum.getTemplateId(), content);
    }


    public Boolean pushWechatTemplateMsg(Integer userId,
                                         String page,
                                         String templateId,
                                         String content) {
        User user = userService.findById(userId);
        String formId = getWechatUserFormId(user.getId());
        if (formId == null) {
            log.error("formId为null,无法给用户推送消息。userId:{};page:{};templateId:{};content:{}", userId, page, templateId, content);
            return false;
        }
        String date = DateUtil.format(new Date(), "yyyy年MM月dd日 HH:mm");
        WxMaTemplateMessage wxMaTemplateMessage = new WxMaTemplateMessage();
        wxMaTemplateMessage.setTemplateId(templateId);
        wxMaTemplateMessage.setToUser(user.getOpenId());
        wxMaTemplateMessage.setPage(page);
        wxMaTemplateMessage.setFormId(formId);
        List<WxMaTemplateMessage.Data> dataList = CollectionUtil.newArrayList(new WxMaTemplateMessage.Data("keyword1", content), new WxMaTemplateMessage.Data("keyword2", date));
        wxMaTemplateMessage.setData(dataList);
        try {
            wxMaService.getMsgService().sendTemplateMsg(wxMaTemplateMessage);
            return true;
        } catch (Exception e) {
            log.error("推送消息出错!", e);
        }
        return false;
    }


    @Override
    public String pushWechatTemplateMsg(String content,
                                        String acceptImId,
                                        String imId) {
        if (redisOpenService.hasKey(RedisKeyEnum.WX_TEMPLATE_MSG.generateKey(imId + "|" + acceptImId))) {
            return "消息已经推送过了!";
        }
        User acceptUser = userService.findByImId(acceptImId);
        if (acceptUser == null || acceptUser.getOpenId() == null) {
            throw new ServiceErrorException("AcceptIM不存在!");
        }
        User sendUser = userService.findByImId(imId);
        if (sendUser == null || sendUser.getOpenId() == null) {
            throw new ServiceErrorException("IM不存在!");
        }
        String formId = getWechatUserFormId(acceptUser.getId());
        if (formId == null) {
            throw new ServiceErrorException("无法给该用户推送消息!");
        }
        String date = DateUtil.format(new Date(), "yyyy年MM月dd日 HH:mm");
        WxMaTemplateMessage wxMaTemplateMessage = new WxMaTemplateMessage();
        wxMaTemplateMessage.setTemplateId(WechatTemplateEnum.PUSH_MSG.getType());
        wxMaTemplateMessage.setToUser(acceptUser.getOpenId());
        wxMaTemplateMessage.setPage("pages/index/index");
        wxMaTemplateMessage.setFormId(formId);
        List<WxMaTemplateMessage.Data> dataList = CollectionUtil.newArrayList(new WxMaTemplateMessage.Data("keyword1", sendUser.getNickname() + ":" + content), new WxMaTemplateMessage.Data("keyword2", date));
        wxMaTemplateMessage.setData(dataList);
        try {
            wxMaService.getMsgService().sendTemplateMsg(wxMaTemplateMessage);
        } catch (Exception e) {
            throw new ServiceErrorException("推送消息出错!");
        }
        //推送状态缓存两个小时
        redisOpenService.set(RedisKeyEnum.WX_TEMPLATE_MSG.generateKey(imId + "|" + acceptImId), imId + "|" + acceptImId, Constant.TIME_MINUTES_FIFTEEN);
        return "消息推送成功!";
    }


    private String getWechatUserFormId(Integer userId) {
        List<WechatFormid> formidList = wechatFormidService.findInSevenDaysFormIdByUser(userId);
        if (formidList.isEmpty()) {
            return null;
        }
        synchronized (objects.get(userId % LOCK_NUM)) {
            WechatFormid formidObj = formidList.get(0);
            try {
                return formidObj.getFormId();
            } finally {
                wechatFormidService.deleteNotAvailableFormIds(formidObj);
            }
        }
    }


}
