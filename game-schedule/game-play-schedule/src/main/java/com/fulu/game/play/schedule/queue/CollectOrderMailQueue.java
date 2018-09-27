package com.fulu.game.play.schedule.queue;

import com.fulu.game.common.properties.Config;
import com.fulu.game.common.utils.MailUtil;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.WechatFormid;
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
public class CollectOrderMailQueue implements Runnable{

    private BlockingQueue<Order> orderMailQueue = new LinkedBlockingDeque<>(50000);

    @Autowired
    private Config configProperties;

    private AtomicBoolean run = new AtomicBoolean();

    @PostConstruct
    public void init() {
        run.set(true);
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.setName("发送未简单的邮件");
        thread.start();
    }

    @PreDestroy
    public void destroy() {
        run.set(false);
    }

    /**
     * 添加formid到队列
     */
    public void addOrderInfo(Order o) {
        orderMailQueue.add(o);
    }


    @Override
    public void run() {
        while (run.get()) {
            try {
                Order o = orderMailQueue.poll();
                if (o == null) {
                    Thread.sleep(300L);
                    continue;
                }

                MailUtil.sendMail(configProperties.getOrdermail().getAddress(),configProperties.getOrdermail().getPassword(),"陪玩师8分钟仍然未接单","陪玩师8分钟仍然未接单，订单号为："+o.getOrderNo(),new String[]{configProperties.getOrdermail().getTargetAddress()});
            } catch (Exception e) {
                log.error("CollectOrderMailQueue run exp", e);
            }
        }
    }

}
