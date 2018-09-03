package com.fulu.game.play.queue;

import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.WechatFormid;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.WechatFormidService;
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
public class CollectFormIdQueue implements Runnable{

    private BlockingQueue<WechatFormid> formIdQueue = new LinkedBlockingDeque<>(50000);

    @Autowired
    private WechatFormidService wechatFormidService;

    @Autowired
    private UserService userService;

    private AtomicBoolean run = new AtomicBoolean();

    @PostConstruct
    public void init() {
        run.set(true);
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.setName("formid收集");
        thread.start();
    }

    @PreDestroy
    public void destroy() {
        run.set(false);
    }

    /**
     * 添加formid到队列
     */
    public void addFormId(WechatFormid wechatFormid) {
        formIdQueue.add(wechatFormid);
    }


    @Override
    public void run() {
        log.info("开始插入formId");
        while (run.get()) {
            try {
                WechatFormid formid = formIdQueue.poll();
                if (formid == null) {
                    Thread.sleep(300L);
                    continue;
                }
                process(formid);
            } catch (Exception e) {
                log.error("WechatformidProcessTask run exp", e);
            }
        }
        log.info("结束收集formId");
    }

    private void process(WechatFormid formid) {
        addFormIdToDB(formid);
    }

    /**
     * 添加收集到的formid到数据库
     */
    private void addFormIdToDB(WechatFormid formid) {
        wechatFormidService.create(formid);
    }


}
