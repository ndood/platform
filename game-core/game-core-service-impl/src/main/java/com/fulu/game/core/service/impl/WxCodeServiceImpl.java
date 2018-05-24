package com.fulu.game.core.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.core.service.WxCodeService;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Service
public class WxCodeServiceImpl implements WxCodeService {
    @Autowired
    private WxMaService wxMaService;
    @Autowired
    private OssUtil ossUtil;

    /**
     * 生成小程序码
     * 使用的是接口B，数量无限制
     *
     * @param scene
     * @param page
     * @return
     * @throws WxErrorException
     */
    @RequestMapping("/create")
    public String create(String scene, String page) throws WxErrorException {
        File file = wxMaService.getQrcodeService().createWxCodeLimit(scene, page);
        //先上传到阿里云，返回图片地址

        if (!file.exists()) {
            return null;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String fileName = file.getName();
        return ossUtil.uploadFile(fis, fileName);
    }
}
