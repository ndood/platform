package com.fulu.game.core.service.impl;

import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.TechAuthStatusEnum;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.common.exception.ApproveException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.dao.ApproveDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Approve;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.ApproveVO;
import com.fulu.game.core.service.*;
import com.xiaoleilu.hutool.util.BeanUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ApproveServiceImpl extends AbsCommonService<Approve, Integer> implements ApproveService {

    @Autowired
    private UserTechAuthService utaService;

    @Autowired
    private UserService userService;

    @Autowired
    private WxTemplateMsgService wxTemplateMsgService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ApproveDao approveDao;

    @Override
    public ICommonDao<Approve, Integer> getDao() {
        return approveDao;
    }

    @Override
    public synchronized ApproveVO save(Integer techAuthId) {
        log.info("====好友认可接口执行====入参技能id:{}", techAuthId);
        UserTechAuth userTechAuth = utaService.findById(techAuthId);
        if (null == userTechAuth) {
            throw new UserException(UserException.ExceptionCode.TECH_AUTH_NOT_EXIST_EXCEPTION);
        }
        if (userTechAuth.getStatus().intValue() == TechAuthStatusEnum.FREEZE.getType().intValue()) {
            throw new ApproveException(ApproveException.ExceptionCode.APPROVE_FREEZE);
        }
        ApproveVO paramVO = new ApproveVO();
        int techOwnerId = userTechAuth.getUserId();
        log.info("技能申请者id:{}", techOwnerId);
        paramVO.setTechOwnerId(techOwnerId);
        User user = userService.getCurrentUser();
        int userId = user.getId();
        log.info("认可人id:{}", userId);
        if (userId == techOwnerId) {
            throw new ApproveException(ApproveException.ExceptionCode.CANNOT_APPROVE_SELF);
        }
        paramVO.setUserId(userId);
        List<Approve> list = approveDao.findByParameter(paramVO);
        if (!CollectionUtil.isEmpty(list)) {
            throw new ApproveException(ApproveException.ExceptionCode.APPROVE_DUPLICATE);
        }
        Approve approve = new Approve();
        approve.setTechAuthId(techAuthId);
        approve.setUserId(userId);
        approve.setTechOwnerId(techOwnerId);
        approve.setCreateTime(new Date());
        approveDao.create(approve);
        log.info("生成认可记录成功");

        int currentCount = userTechAuth.getApproveCount();
        int newApproveCount = currentCount + 1;
        int requireCount = newApproveCount < Constant.DEFAULT_APPROVE_COUNT ? Constant.DEFAULT_APPROVE_COUNT - newApproveCount : 0;
        userTechAuth.setApproveCount(newApproveCount);
        int techStatus;
        WechatTemplateMsgEnum wechatTemplateMsgEnum;
        if (newApproveCount >= Constant.DEFAULT_APPROVE_COUNT) {
            techStatus = TechAuthStatusEnum.NORMAL.getType();
            wechatTemplateMsgEnum = WechatTemplateMsgEnum.TECH_AUTH_AUDIT_SUCCESS;
        } else {
            techStatus = TechAuthStatusEnum.AUTHENTICATION_ING.getType();
            wechatTemplateMsgEnum = WechatTemplateMsgEnum.TECH_AUTH_AUDIT_ING;
        }
        userTechAuth.setStatus(techStatus);
        utaService.update(userTechAuth);
        log.info("更新t_user_tech_auth表,当前认可数:{}", newApproveCount);
        if (techStatus == TechAuthStatusEnum.NORMAL.getType()) {
            wxTemplateMsgService.pushWechatTemplateMsg(techOwnerId, wechatTemplateMsgEnum, user.getNickname(), String.valueOf(requireCount));
            log.info("好友认可-调用发送通知接口完成");
            productService.recoverProductDelFlagByTechAuthId(techAuthId);
        } else {
            wxTemplateMsgService.pushWechatTemplateMsg(techOwnerId, wechatTemplateMsgEnum);
            log.info("好友认可-调用发送通知接口完成");
        }
        ApproveVO responseVO = new ApproveVO();
        BeanUtil.copyProperties(approve, responseVO);
        responseVO.setTechStatus(techStatus);
        responseVO.setApproveCount(newApproveCount);
        responseVO.setRequireCount(requireCount);
        return responseVO;
    }

    @Override
    public List<Approve> findByParam(ApproveVO approveVO) {
        return approveDao.findByParameter(approveVO);
    }

    @Override
    public void delByTechAuthId(Integer techAuthId) {
        log.info("删除技能认可记录，入参techAuthId:{}", techAuthId);
        approveDao.delByTechAuthId(techAuthId);
    }

    @Override
    public void resetApproveStatusAndUpdate(UserTechAuth userTechAuth) {
        userTechAuth.setApproveCount(0);
        utaService.update(userTechAuth);
        delByTechAuthId(userTechAuth.getId());
    }

}
