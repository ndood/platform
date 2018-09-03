package com.fulu.game.admin.controller;

import cn.hutool.core.date.DateUtil;
import com.fulu.game.admin.service.AdminPushMsgService;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.PushMsgTypeEnum;
import com.fulu.game.core.entity.PushMsg;
import com.fulu.game.core.entity.vo.PushMsgVO;
import com.fulu.game.core.service.PushMsgService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/api/v1/pushmsg")
public class PushMsgController extends BaseController {

    private final AdminPushMsgService adminPushMsgService;

    @Autowired
    public PushMsgController(AdminPushMsgService adminPushMsgService) {
        this.adminPushMsgService = adminPushMsgService;
    }

    /**
     * 推送微信消息
     *
     * @param pushMsgVO
     * @return
     */
    @PostMapping(value = "/push")
    public Result push(@Valid PushMsgVO pushMsgVO) {
        if (PushMsgTypeEnum.ASSIGN_USERID.getType().equals(pushMsgVO.getType())) {
            if (StringUtils.isBlank(pushMsgVO.getPushIds())) {
                return Result.error().msg("指定用户不能为空!");
            }
        }
        if (pushMsgVO.getTouchTime() != null) {
            if (DateUtil.beginOfDay(pushMsgVO.getTouchTime()).before(DateUtil.beginOfDay(new Date()))) {
                return Result.error().msg("推送日期不能小于当前日期!");
            }
        }
        long startTime = System.currentTimeMillis();
        adminPushMsgService.push(pushMsgVO);
        long endTime = System.currentTimeMillis();
        log.info("pushmsg.push方法耗时:{}", endTime - startTime);
        return Result.success();
    }

    /**
     * 推送消息列表
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数据条数
     * @param platform 平台类型
     * @return 分页结果集
     */
    @PostMapping(value = "/list")
    public Result list(Integer pageNum, Integer pageSize, Integer platform) {
        PageInfo<PushMsg> page = adminPushMsgService.list(pageNum, pageSize, platform, null);
        return Result.success().data(page);
    }
}
