package com.java.test;

import com.fulu.game.admin.controller.SharingController;
import com.fulu.game.core.entity.vo.SharingVO;
import com.fulu.game.core.service.SharingService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SharingControllerTest {

    @Autowired
    private SharingController sharingController;
    @Autowired
    private SharingService sharingService;
    @Test
    public void save() throws Exception {
        SharingVO sharingVO = new SharingVO();
        sharingVO.setContent("xx正在申请陪玩师，长按识别二维码认可他的能力");
        sharingVO.setGender(1);
        sharingVO.setShareType(1);
        sharingController.save(sharingVO);
    }

    //@Test
    public void update() throws Exception {
        sharingController.update(1,true,1,1,"aaaa");
        Assert.assertEquals("aaaa",sharingService.findById(1).getContent());
    }

    //@Test
    public void delete() throws Exception {
        sharingController.delete(1);
        Assert.assertNull(sharingService.findById(1));
    }

    //@Test
    public void list() throws Exception {

    }

}