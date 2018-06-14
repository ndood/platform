package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.exception.CashException;
import com.fulu.game.core.entity.Channel;
import com.fulu.game.core.entity.ChannelCashDetails;
import com.fulu.game.core.entity.vo.ChannelVO;
import com.fulu.game.core.service.ChannelCashDetailsService;
import com.fulu.game.core.service.ChannelService;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/channel")
@Slf4j
public class ChannelController {

    @Autowired
    private ChannelService channelService;
    @Autowired
    private ChannelCashDetailsService channelCDService;

    /**
     * 新增渠道商
     *
     * @param name
     * @return
     */
    @PostMapping("/save")
    public Result save(@RequestParam("name") String name) {
        log.info("调用新增渠道商接口，入参:name={}", name);
        //查重名
        ChannelVO channelVO = new ChannelVO();
        channelVO.setName(name);
        List<Channel> channelList = channelService.findByParam(channelVO);
        if (!CollectionUtil.isEmpty(channelList)) {
            return Result.error().msg("该渠道商名已存在,请重新输入");
        }
        Channel channel = channelService.save(name);
        return Result.success().data(channel).msg("操作成功");
    }

    /**
     * 修改
     *
     * @param id
     * @param name
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestParam("id") Integer id, @RequestParam("name") String name) {
        //查重名
        ChannelVO channelVO = new ChannelVO();
        channelVO.setName(name);
        List<Channel> channelList = channelService.findByParam(channelVO);
        if (!CollectionUtil.isEmpty(channelList)) {
            return Result.error().msg("该渠道商名已存在,请重新输入");
        }
        Channel channel = channelService.update(id, name);
        return Result.success().data(channel).msg("操作成功");
    }

    /**
     * token重新生成
     *
     * @return
     */
    @PostMapping("/token/recreate")
    public Result recreate(@RequestParam("id") Integer id) {
        log.info("调用token重生成接口，入参:id={}", id);
        String token = channelService.recreate(id);
        return Result.success().data(token).msg("操作成功");
    }

    /**
     * 渠道商列表
     *
     * @return
     */
    @PostMapping("/list")
    public Result list(@RequestParam("pageNum") Integer pageNum,
                       @RequestParam("pageSize") Integer pageSize) {
        PageInfo<Channel> resultPage = channelService.list(pageNum, pageSize);
        return Result.success().data(resultPage);
    }

    /**
     * 统计总下单数+成功订单数+总消费金额
     *
     * @param groupId
     * @return
     */
    @PostMapping("/stats")
    public Result stats(@RequestParam("groupId") Integer groupId) {
        //todo 渠道商统计
        return Result.success().data(1);
    }

    /**
     * 加款
     *
     * @param channelId
     * @param money
     * @param remark
     * @return
     */
    @PostMapping("/cash/add")
    public Result addCash(@RequestParam("id") Integer channelId,
                          @RequestParam("money") BigDecimal money,
                          @RequestParam("remark") String remark) {
        log.info("调用渠道商加款接口，入参channelId={}，money={}，remark={}", channelId, money, remark);
        log.info("===开始数据校验===");
        if (money.compareTo(BigDecimal.ZERO) == -1) {
            throw new CashException(CashException.ExceptionCode.CASH_NEGATIVE_EXCEPTION);
        }
        ChannelCashDetails channelCashDetails = channelCDService.addCash(channelId, money, remark);
        return Result.success().data(channelCashDetails).msg("操作成功");
    }
}
