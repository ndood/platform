package com.fulu.game.core.service.queue;

import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.fulu.game.common.Constant;
import com.fulu.game.common.config.WxMaServiceSupply;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.enums.SMSTemplateEnum;
import com.fulu.game.common.enums.WechatTemplateIdEnum;
import com.fulu.game.core.entity.PushMsg;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserInfoAuth;
import com.fulu.game.core.entity.WxMaTemplateMessageVO;
import com.fulu.game.core.entity.vo.SMSVO;
import com.fulu.game.core.service.PushMsgService;
import com.fulu.game.core.service.UserInfoAuthService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Slf4j
@Component
public class MiniAppPushContainer extends RedisTaskContainer {

    private static final String MINI_APP_PUSH_QUEQUE = "miniapp:push:queue";
    private static int runTaskThreadNum = 1;
    //使用一个统一维护的线程池来管理隔离线程
    protected static ExecutorService es = Executors.newFixedThreadPool(runTaskThreadNum);
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    private RedisConsumer redisConsumer;

    @Autowired
    private WxMaServiceSupply wxMaServiceSupply;
    @Autowired
    private PushMsgService pushMsgServiceImpl;
    @Autowired
    private UserService userService;
    @Qualifier(value = "userInfoAuthServiceImpl")
    @Autowired
    private UserInfoAuthService userInfoAuthService;
    @Autowired
    private SMSPushContainer smsPushContainer;

    @PostConstruct
    private void init() {
        redisQueue = new RedisQueue<WxMaTemplateMessageVO>(MINI_APP_PUSH_QUEQUE, redisOpenService);

        if (!configProperties.getQueue().isMiniappPush()) {
            log.info("无需开启小程序推送队列消费线程");
            es.shutdown();
            return;
        }
        Consumer<WxMaTemplateMessageVO> consumer = (data) -> {
            process(data);
        };

        //提交线程
        for (int i = 0; i < runTaskThreadNum; i++) {
            redisConsumer = new RedisConsumer<>(this, consumer);
            es.execute(redisConsumer);
        }
    }

    private void process(WxMaTemplateMessageVO wxMaTemplateMessageVO) {
        User user = userService.findByOpenId(wxMaTemplateMessageVO.getToUser(),
                PlatformEcoEnum.getEnumByType(wxMaTemplateMessageVO.getPlatform()));
        UserInfoAuth userInfoAuth = userInfoAuthService.findByUserId(user.getId());
        if (userInfoAuth != null && userInfoAuth.getVestFlag()) {
            log.info("马甲用户，不发通知");
            return;
        }
        WxMaTemplateMessage wxMaTemplateMessage = null;
        try {
            log.info("推送消息队列推送消息:wxMaTemplateMessageVO:{}", wxMaTemplateMessageVO);
            wxMaTemplateMessage = WxMaTemplateMessage.builder().templateId(wxMaTemplateMessageVO.getTemplateId())
                    .data(JSON.parseArray(wxMaTemplateMessageVO.getDataJson(), WxMaTemplateMessage.Data.class))
                    .page(wxMaTemplateMessageVO.getPage())
                    .formId(wxMaTemplateMessageVO.getFormId())
                    .toUser(wxMaTemplateMessageVO.getToUser())
                    .build();

            Integer pushId = wxMaTemplateMessageVO.getPushId();
            if (PlatformEcoEnum.POINT.getType().equals(wxMaTemplateMessageVO.getPlatform())) {
                log.info("上分平台推送消息:wxMaTemplateMessageVO:{}", wxMaTemplateMessageVO);
                wxMaServiceSupply.pointWxMaService().getMsgService().sendTemplateMsg(wxMaTemplateMessage);
            } else {
                log.info("陪玩平台推送消息:wxMaTemplateMessageVO:{}", wxMaTemplateMessageVO);
                wxMaServiceSupply.playWxMaService().getMsgService().sendTemplateMsg(wxMaTemplateMessage);
            }
            if (pushId != null) {
                countPushSuccessNum(wxMaTemplateMessageVO.getPushId());
            }
            log.info("推送微信模板消息:{}", wxMaTemplateMessage.toJson());
        } catch (WxErrorException we) {
            sendSMSIfFail(wxMaTemplateMessage, user);
            log.error("推送微信消息异常", we);
        } catch (Exception e) {
            log.error("推送消息出错!", e);
        }
    }

    /**
     * 计算消息推送成功数
     *
     * @param pushId
     */
    private void countPushSuccessNum(int pushId) {
        PushMsg pushMsg = pushMsgServiceImpl.findById(pushId);
        int successNum = pushMsg.getSuccessNum();
        PushMsg paramPushMsg = new PushMsg();
        paramPushMsg.setId(pushId);
        paramPushMsg.setSuccessNum(successNum + 1);
        paramPushMsg.setUpdateTime(DateUtil.date());
        pushMsgServiceImpl.update(paramPushMsg);
        log.info("更新消息推送成功数:{}", paramPushMsg);
    }

    /**
     * 添加小程序推送元素到队列
     */
    public void add(WxMaTemplateMessageVO wxMaTemplateMessageVO) {
        redisQueue.pushFromHead(wxMaTemplateMessageVO);
    }

    /**
     * 微信推送失败，补发短信
     *
     * @param wxMaTemplateMessage
     */
    private void sendSMSIfFail(WxMaTemplateMessage wxMaTemplateMessage, User user) {
        if (wxMaTemplateMessage == null) {
            log.info("补发短信wxMaTemplateMessageVO不能为null");
            return;
        }
        String mobile = user.getMobile();
        if (StringUtils.isNotEmpty(mobile)) {
            WechatTemplateIdEnum wechatTemplateIdEnum = WechatTemplateIdEnum.getWxTemplate(wxMaTemplateMessage.getTemplateId());
            if (wechatTemplateIdEnum == null) {
                log.error("补发短信中短信模板非法");
                return;
            }
            SMSTemplateEnum smsTemplate = wechatTemplateIdEnum.getSmsTemplate();
            List<WxMaTemplateMessage.Data> dataList = wxMaTemplateMessage.getData();
            String content = wechatTemplateIdEnum.getContent();
            for (WxMaTemplateMessage.Data data : dataList) {
                StringUtils.replace(content, data.getName(), data.getValue());
            }
            String[] params = {content, Constant.WEIXN_JUMP_URL};
            SMSVO smsvo = new SMSVO(user.getMobile(), smsTemplate, params);
            smsPushContainer.add(smsvo);
        }
    }

    @PreDestroy
    public void destroy() {
        log.info("小程序推送队列销毁");
        if (es != null) {
            es.shutdown();
        }
        if (redisConsumer != null) {
            redisConsumer.shutdown();
        }
    }
}
