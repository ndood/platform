package com.java.test;

import com.fulu.game.admin.AdminApplication;
import com.fulu.game.admin.Task;
import com.fulu.game.core.entity.CouponGroup;
import com.fulu.game.core.entity.ImUser;
import com.fulu.game.core.service.CouponGroupService;
import com.fulu.game.core.service.ImService;
import com.xiaoleilu.hutool.date.DateUnit;
import com.xiaoleilu.hutool.date.DateUtil;
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
    private ImService imService;



    @Test
    public void test1()throws Exception{
        String imId = "s87_20180629";
        String imPwd = "123456";
        ImUser imUser =imService.registerUser(imId,imPwd);
        System.out.println(imUser);
    }

}
