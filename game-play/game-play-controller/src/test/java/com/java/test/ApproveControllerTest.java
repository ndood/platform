package com.java.test;

import com.fulu.game.core.service.ApproveService;
import com.fulu.game.play.PlayApplication;
import com.fulu.game.play.controller.ApproveController;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PlayApplication.class)
@Slf4j
public class ApproveControllerTest {
    @Autowired
    private ApproveController approveController;

    @Test
    public void testSave() {
        int count = 100;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(count);
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        for (int i = 0; i < count; i++) {
            executorService.execute(new ApproveControllerTest().new Task(cyclicBarrier));
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class Task implements Runnable {
        private CyclicBarrier cyclicBarrier;

        public Task(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void run() {
            try {
                // 等待所有任务准备就绪
                cyclicBarrier.await();
                approveController.save(31);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}