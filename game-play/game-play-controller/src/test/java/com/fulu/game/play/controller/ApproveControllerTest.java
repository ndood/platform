package com.fulu.game.play.controller;

import com.fulu.game.core.service.ApproveService;
import com.fulu.game.play.PlayApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PlayApplication.class)
@Slf4j
@AutoConfigureMockMvc
public class ApproveControllerTest {

    @Autowired
    private ApproveService approveService;

    @Test
    public void testSave() {
        int count = 100;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(count);
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        for (int i = 0; i < count; i++) {
            executorService.execute(new Task(cyclicBarrier, approveService));
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class Task implements Runnable {
        private CyclicBarrier cyclicBarrier;
        private ApproveService as;

        public Task(CyclicBarrier cyclicBarrier, ApproveService approveService) {
            this.cyclicBarrier = cyclicBarrier;
            this.as = approveService;
        }

        @Override
        public void run() {
            try {
                // 等待所有任务准备就绪
                cyclicBarrier.await();
                as.save(31);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}