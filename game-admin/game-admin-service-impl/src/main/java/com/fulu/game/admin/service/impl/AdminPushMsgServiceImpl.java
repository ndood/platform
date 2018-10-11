package com.fulu.game.admin.service.impl;

import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.fulu.game.admin.service.AdminPushMsgService;
import com.fulu.game.core.entity.vo.AppPushMsgVO;
import com.fulu.game.core.service.impl.push.AppPushServiceImpl;
import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.utils.AppRouteFactory;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.PushMsg;
import com.fulu.game.core.entity.vo.PushMsgVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.PushMsgServiceImpl;
import com.fulu.game.core.service.impl.push.PushServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.*;

@Slf4j
@Service
public class AdminPushMsgServiceImpl extends PushMsgServiceImpl implements AdminPushMsgService {


    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;
    @Qualifier(value = "pushServiceImpl")
    @Autowired
    private PushServiceImpl pushService;

    @Autowired
    private AppPushServiceImpl mobileAppPushService;

    @Override
    public void push(PushMsgVO pushMsgVO) {
        Admin admin = adminService.getCurrentUser();
        pushMsgVO.setAdminId(admin.getId());
        pushMsgVO.setAdminName(admin.getName());
        pushMsgVO.setCreateTime(new Date());
        pushMsgVO.setUpdateTime(new Date());
        pushMsgVO.setHits(0L);
        pushMsgVO.setTotalNum(0);
        pushMsgVO.setSuccessNum(0);
        pushMsgVO.setIsPushed(false);
        create(pushMsgVO);
        log.info("管理员添加推送记录:pushMsgVO:{}", pushMsgVO);
        if (pushMsgVO.getTouchTime() == null) {
            log.info("实时推送微信消息执行:pushMsgVO:{};", pushMsgVO);
            exePushTask(pushMsgVO);
        }
    }


    private void exePushTask(PushMsg pushMsg) {
        log.info("开始推送微信消息:pushMsg:{};", pushMsg);
        List<Integer> userIds = null;
        int count;
        if (PushMsgTypeEnum.ALL_USER.getType().equals(pushMsg.getType())) {
            count = userService.countAllNormalUser();
        } else if (PushMsgTypeEnum.ASSIGN_USERID.getType().equals(pushMsg.getType())) {
            userIds = new ArrayList<>();
            try {
                String[] userArr = pushMsg.getPushIds().split(",");
                if (userArr.length == 0) {
                    throw new ServiceErrorException("指定用户ID为空!");
                }
                for (int i = 0; i < userArr.length; i++) {
                    userIds.add(Integer.valueOf(userArr[i]));
                }
                count = userIds.size();
            } catch (Exception e) {
                log.error("指定用户推送错误:", e);
                throw new ServiceErrorException("指定用户输入错误!");
            }
        } else if (PushMsgTypeEnum.ALL_SERVICE_USER.getType().equals(pushMsg.getType())) {
            count = userService.countAllServiceUser();
        } else {
            throw new ServiceErrorException("推送类型指定错误!");
        }

        try {
            String lastPage = new StringBuffer(WechatPagePathEnum.PUSH_PAGE.getPlayPagePath())
                    .append("?redirect=")
                    .append(URLEncoder.encode(pushMsg.getPage(), "utf-8"))
                    .append("&pushId=")
                    .append(pushMsg.getId()).toString();
            log.info("开始执行推送消息:userId:{};lastPage:{};pushMsg:{};", userIds == null ? "全体用户" : userIds, lastPage, pushMsg);
            long startTime = System.currentTimeMillis();

            adminPushWxTemplateMsg(pushMsg, userIds, lastPage);

            long endTime = System.currentTimeMillis();
            log.info("pushTask:{}执行wxTemplateMsgService.adminPushWxTemplateMsg方法耗时:{}", pushMsg.getId(), endTime - startTime);
            pushMsg.setTotalNum(count);
            pushMsg.setIsPushed(true);
            pushMsg.setUpdateTime(new Date());
            pushMsg.setTouchTime(new Date());
            update(pushMsg);
        } catch (Exception e) {
            log.error("消息推送urlencode exp", e);
        }

    }


    @Override
    public void appointPush(PushMsg pushMsg) {
        if (pushMsg == null || pushMsg.getTouchTime() == null) {
            return;
        }
        if (!pushMsg.getIsPushed() && pushMsg.getTouchTime().before(new Date())) {
            log.info("指定时间推送微信消息执行:pushMsg:{};", pushMsg);
            exePushTask(pushMsg);
        }
    }


    public void adminPushWxTemplateMsg(PushMsg pushMsg, List<Integer> userIds, String page) {
        String date = DateUtil.format(new Date(), "yyyy年MM月dd日 HH:mm");
        List<WxMaTemplateMessage.Data> dataList = CollectionUtil.newArrayList(
                new WxMaTemplateMessage.Data("keyword1", pushMsg.getContent()),
                new WxMaTemplateMessage.Data("keyword2", date));
        if (PlatformEcoEnum.PLAY.getType().equals(pushMsg.getPlatform())) {
            pushService.addTemplateMsg2Queue(pushMsg.getPlatform(), pushMsg.getId(), userIds, page, WechatTemplateIdEnum.PLAY_LEAVE_MSG, dataList);
        } else if (PlatformEcoEnum.POINT.getType().equals(pushMsg.getPlatform())) {
            pushService.addTemplateMsg2Queue(pushMsg.getPlatform(), pushMsg.getId(), userIds, page, WechatTemplateIdEnum.POINT_LEAVE_MSG, dataList);
        } else if (PlatformEcoEnum.APP.getType().equals(pushMsg.getPlatform())) {
            Map<String, String> extras = null;
            switch (PushMsgJumpTypeEnum.convert(pushMsg.getJumpType())) {
                case OFFICIAL_NOTICE:
                    extras = AppRouteFactory.buildOfficialNoticeRoute();
                    break;
                default:
                    extras = AppRouteFactory.buildIndexRoute();
            }
            AppPushMsgVO appPushMsgVO = null;
            if (PushMsgTypeEnum.ALL_USER.getType().equals(pushMsg.getType())) {
                appPushMsgVO = AppPushMsgVO.newSendAllBuilder().title(pushMsg.getTitle()).alert(pushMsg.getContent()).extras(extras).build();
            } else {
                appPushMsgVO = AppPushMsgVO.newBuilder(userIds.toArray(new Integer[]{})).title(pushMsg.getTitle()).alert(pushMsg.getContent()).extras(extras).build();
            }
            mobileAppPushService.pushMsg(appPushMsgVO);

        }
    }

}
