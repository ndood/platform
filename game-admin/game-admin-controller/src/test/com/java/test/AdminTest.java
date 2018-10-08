package com.java.test;

import com.fulu.game.admin.AdminApplication;
import com.fulu.game.common.domain.Password;
import com.fulu.game.common.enums.AdminStatus;
import com.fulu.game.common.utils.EncryptUtil;
import com.fulu.game.core.entity.Admin;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AdminApplication.class)
@Slf4j
public class AdminTest {

    @Test
    public void test1() {
        Admin admin = createAdminAccount("龚泽淳", "gongzc", "gongzc123456");
        log.info("admin:{}", admin);



    }


    public Admin createAdminAccount(String name, String username, String pword) {
        Admin admin = new Admin();
        admin.setName(name);
        admin.setUsername(username);
        admin.setStatus(AdminStatus.ENABLE.getType());
        Password password = EncryptUtil.PiecesEncode(pword);
        admin.setPassword(password.getPassword());
        admin.setSalt(password.getSalt());
        admin.setCreateTime(new Date());
        admin.setUpdateTime(admin.getCreateTime());
        return admin;
    }


}
