package com.java.test;

import com.fulu.game.admin.AdminApplication;
import com.fulu.game.admin.Task;
import com.fulu.game.core.entity.CouponGroup;
import com.fulu.game.core.service.CouponGroupService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AdminApplication.class)
@Slf4j
public class AsyncTest {

    @Autowired
    private Task task;
    @Autowired
    private CouponGroupService couponGroupService;



    @Test
    public void test1()throws Exception{
        CouponGroup couponGroup = new CouponGroup();
        couponGroup.setDeduction(new BigDecimal(10));
        couponGroup.setAmount(10);
        couponGroup.setCreateTime(new Date());
        couponGroup.setIsNewUser(true);
        couponGroup.setRedeemCode("x");
        couponGroup.setRemark("s");
        couponGroup.setStartUsefulTime(new Date());
        couponGroup.setEndUsefulTime(new Date());
        couponGroupService.create(couponGroup);
//        couponGroupService.batchGenerateCoupon(couponGroup);
        System.out.println("拉拉");
    }
}
