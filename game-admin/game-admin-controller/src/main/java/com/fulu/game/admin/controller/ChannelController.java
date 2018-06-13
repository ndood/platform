package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Channel;
import com.fulu.game.core.entity.vo.ChannelVO;
import com.fulu.game.core.service.ChannelService;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/channel")
@Slf4j
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    /**
     * 新增渠道商
     * @param name
     * @return
     */
    @RequestMapping("/save")
    public Result save(@RequestParam("name") String name) {
        log.info("调用新增渠道商接口，入参:name={}",name);
        //查重名
        ChannelVO channelVO = new ChannelVO();
        channelVO.setName(name);
        List<Channel> channelList = channelService.findByParam(channelVO);
        if (!CollectionUtil.isEmpty(channelList)){
            return Result.error().msg("该渠道商名已存在,请重新输入");
        }
        Channel channel = channelService.save(name);
        return Result.success().data(channel).msg("操作成功");
    }

    /**
     * token重新生成
     *
     * @return
     */
    @RequestMapping("/token/recreate")
    public Result recreate(@RequestParam("id") Integer id) {
        //channelService.recreate();
        return null;
    }

    public Result update(@RequestParam("id") Integer id,@RequestParam("name") String name){
        Channel channel = channelService.findById(id);
        channel.setName(name);
        return null;
    }

    /**
     * 渠道商列表
     *
     * @return
     */
    public Result list() {
        return null;
    }
}
