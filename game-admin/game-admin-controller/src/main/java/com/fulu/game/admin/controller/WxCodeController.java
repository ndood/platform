package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.config.WxMaServiceSupply;
import com.fulu.game.common.utils.OssUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Slf4j
@RestController
@RequestMapping("/api/v1/wxcode")
public class WxCodeController {

    @Autowired
    private WxMaServiceSupply wxMaServiceSupply;
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
    public Result wxaCreate(@RequestParam("scene") String scene,
                            @RequestParam("page") String page) throws WxErrorException {

        //todo 这里需要判断是上分还是陪玩
        File file = wxMaServiceSupply.gameWxMaService().getQrcodeService().createWxCodeLimit(scene, page);
        //先上传到阿里云，返回图片地址

        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String fileName = file.getName();
            String wxcodeUrl = ossUtil.uploadFile(fis, fileName);
            return Result.success().data("wxcodeUrl", wxcodeUrl).msg("生成小程序码成功！");
        } else {
            return Result.error().msg("获取小程序码失败！");
        }
    }

}
