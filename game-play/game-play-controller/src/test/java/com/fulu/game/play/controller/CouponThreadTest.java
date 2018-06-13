package com.fulu.game.play.controller;

import com.fulu.game.core.service.CouponService;
import com.fulu.game.play.PlayApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PlayApplication.class)
@Slf4j
@AutoConfigureMockMvc
public class CouponThreadTest {

    @Autowired
    private CouponService couponService;

    @Test
    public void test1()throws Exception{

        int count = 10;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(count);

        ExecutorService executorService = Executors.newFixedThreadPool(count);
        for (int i = 0; i < count; i++) {
            executorService.execute( new Runnable() {
                @Override
                public void run() {
                    try {
                        cyclicBarrier.await();
                        couponService.generateCoupon("110",36,new Date(),"127.0.0.1");
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
        }

    }


}
