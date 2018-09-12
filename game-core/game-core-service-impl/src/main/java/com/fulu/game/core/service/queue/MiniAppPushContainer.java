package com.fulu.game.core.service.queue;

import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.config.WxMaServiceSupply;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.core.entity.PushMsg;
import com.fulu.game.core.entity.WxMaTemplateMessageVO;
import com.fulu.game.core.service.PushMsgService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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

    @PostConstruct
    private void init() {
        log.info("play小程序推送队列线程");
        if (!configProperties.getQueue().isMiniappPush()) {
            log.info("无需开启小程序推送队列线程");
            es.shutdown();
            return;
        }
        log.info("play小程序已经开启了推送队列线程");
        redisQueue = new RedisQueue<WxMaTemplateMessageVO>(MINI_APP_PUSH_QUEQUE, redisOpenService);
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
        //TODO 直接贴的之前的消费小程序推送队列的代码，因为之前代码是把app和小程序推送揉到一起，所以这里需要拆分出来，
        //TODO app的要单独拆开放到appPushContainer的process里
        try {
            log.info("推送消息队列推送消息:wxMaTemplateMessageVO:{}", wxMaTemplateMessageVO);
            WxMaTemplateMessage wxMaTemplateMessage = WxMaTemplateMessage.builder().templateId(wxMaTemplateMessageVO.getTemplateId())
                    .data(wxMaTemplateMessageVO.getData())
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
