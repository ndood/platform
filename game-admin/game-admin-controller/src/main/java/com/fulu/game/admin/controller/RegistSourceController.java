package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.RegistSource;
import com.fulu.game.core.entity.vo.RegistSourceVO;
import com.fulu.game.core.service.RegistSourceService;
import com.xiaoleilu.hutool.util.CollectionUtil;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/regist-source")
public class RegistSourceController {

    @Autowired
    private RegistSourceService rsService;

    /**
     * 新增注册来源
     *
     * @param name
     * @param remark
     * @return
     * @throws WxErrorException
     */
    @RequestMapping("/save")
    public Result save(@RequestParam("name") String name,
                       @RequestParam("remark") String remark) throws WxErrorException {

        //查重名
        RegistSourceVO rsVO = new RegistSourceVO();
        rsVO.setName(name);
        List<RegistSource> rsList = rsService.findByParam(rsVO);
        if (!CollectionUtil.isEmpty(rsList)) {
            return Result.error().msg("该名称已存在");
        }
        RegistSource rs = rsService.save(name, remark);
        return Result.success().data(rs).msg("添加成功");
    }
}
