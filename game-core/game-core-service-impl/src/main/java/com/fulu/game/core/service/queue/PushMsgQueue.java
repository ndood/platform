package com.fulu.game.core.service.queue;


import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.config.WxMaServiceSupply;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.core.entity.PushMsg;
import com.fulu.game.core.entity.WxMaTemplateMessageVO;
import com.fulu.game.core.service.PushMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class PushMsgQueue implements Runnable {

    private BlockingQueue<WxMaTemplateMessageVO> templateMessageQueue = new LinkedBlockingDeque<>(50000);

    private AtomicBoolean run = new AtomicBoolean();

    @Autowired
    private WxMaServiceSupply wxMaServiceSupply;

    @Autowired
    private PushMsgService pushMsgServiceImpl;

    @PostConstruct
    public void init() {
        run.set(true);
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.setName("推送微信模板消息开启");
        thread.start();
    }


    @PreDestroy
    public void destroy() {
        run.set(false);
    }


    public void addTemplateMessage(WxMaTemplateMessageVO wxMaTemplateMessageVO) {
        templateMessageQueue.add(wxMaTemplateMessageVO);
    }


    @Override
    public void run() {
        log.info("开始推送微信模板消息");
        while (run.get()) {
            try {
                WxMaTemplateMessageVO wxMaTemplateMessageVO = templateMessageQueue.poll();
                if (wxMaTemplateMessageVO == null) {
                    Thread.sleep(300L);
                    continue;
                }
                process(wxMaTemplateMessageVO);
            } catch (Exception e) {
                log.error("推送微信模板消息异常", e);
            }
        }
        log.info("结束推送微信模板消息");
    }

    //todo 改成redis队列
    private void process(WxMaTemplateMessageVO wxMaTemplateMessageVO) {
        try {
            log.info("推送消息队列推送消息:wxMaTemplateMessageVO:{}", wxMaTemplateMessageVO);
            WxMaTemplateMessage wxMaTemplateMessage = wxMaTemplateMessageVO.getWxMaTemplateMessage();
            Integer pushId = wxMaTemplateMessageVO.getPushId();
            if (PlatformEcoEnum.POINT.getType().equals(wxMaTemplateMessageVO.getPlatform())) {
                log.info("上分平台推送消息:wxMaTemplateMessageVO:{}", wxMaTemplateMessageVO);
                wxMaServiceSupply.pointWxMaService().getMsgService().sendTemplateMsg(wxMaTemplateMessage);
            } else if (PlatformEcoEnum.APP.getType().equals(wxMaTemplateMessageVO.getPlatform())) {
                //todo APP推送

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
}
