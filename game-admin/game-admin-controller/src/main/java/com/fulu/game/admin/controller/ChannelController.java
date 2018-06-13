package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.service.ChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/channel")
@Slf4j
public class ChannelController {
    @Autowired
    private ChannelService channelService;

    /**
     * 新增渠道商
     *
     * @return
     */
    public Result save() {
        return null;
    }

    /**
     * 新增渠道商
     *
     * @return
     */
    public Result update() {
        return null;
    }

    /**
     * 新增渠道商
     *
     * @return
     */
    public Result list() {
        return null;
    }
}
