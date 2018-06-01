package com.fulu.game.core.service.impl;

import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.TechAuthStatusEnum;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.common.exception.ApproveException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.TimeUtil;
import com.fulu.game.core.dao.ApproveDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Approve;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.ApproveVO;
import com.fulu.game.core.service.ApproveService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.UserTechAuthService;
import com.fulu.game.core.service.WxTemplateMsgService;
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
    private ApproveDao approveDao;

    @Override
    public ICommonDao<Approve, Integer> getDao() {
        return approveDao;
    }

    @Override
    public ApproveVO save(Integer techAuthId) {
        log.info("====好友认可接口执行====入参技能id:{},时间:{}", techAuthId, TimeUtil.defaultFormat(new Date()));
        UserTechAuth userTechAuth = utaService.findById(techAuthId);
        if (null == userTechAuth) {
            throw new UserException(UserException.ExceptionCode.TECH_AUTH_NOT_EXIST_EXCEPTION);
        }
        if (userTechAuth.getStatus() == TechAuthStatusEnum.FREEZE.getType()) {
            throw new ApproveException(ApproveException.ExceptionCode.APPROVE_FREEZE);
        }
        ApproveVO paramVO = new ApproveVO();
        Integer techOwnerId = userTechAuth.getUserId();
        log.info("技能申请者id:{}", techOwnerId);
        paramVO.setTechOwnerId(techOwnerId);
        User user = userService.getCurrentUser();
        log.info("认可人id:{}", user.getId());
        if (user.getId() == techOwnerId){
            throw new ApproveException(ApproveException.ExceptionCode.CANNOT_APPROVE_SELF);
        }
        paramVO.setUserId(user.getId());
        List<Approve> list = approveDao.findByParameter(paramVO);
        if (!CollectionUtil.isEmpty(list)) {
            throw new ApproveException(ApproveException.ExceptionCode.APPROVE_DUPLICATE);
        }
        Approve approve = new Approve();
        approve.setTechAuthId(techAuthId);
        approve.setUserId(user.getId());
        approve.setTechOwnerId(techOwnerId);
        approve.setCreateTime(new Date());
        approveDao.create(approve);

        Integer currentCount = userTechAuth.getApproveCount();
        Integer newApproveCount = currentCount + 1;
        userTechAuth.setApproveCount(newApproveCount);
        if (newApproveCount >= Constant.DEFAULT_APPROVE_COUNT) {
            userTechAuth.setStatus(TechAuthStatusEnum.NORMAL.getType());
        } else {
            userTechAuth.setStatus(TechAuthStatusEnum.AUTHENTICATION_ING.getType());
        }
        utaService.update(userTechAuth);

        UserTechAuth nowUserTechAuth = utaService.findById(userTechAuth.getId());
        Integer techStatus = nowUserTechAuth.getStatus();
        Integer approveCount = nowUserTechAuth.getApproveCount();
        Integer requireCount = approveCount < 5 ? Constant.DEFAULT_APPROVE_COUNT - approveCount : 0;
        if (techStatus == TechAuthStatusEnum.NORMAL.getType()) {
            wxTemplateMsgService.pushWechatTemplateMsg(nowUserTechAuth.getUserId(), WechatTemplateMsgEnum.TECH_AUTH_AUDIT_SUCCESS);
        } else {
            wxTemplateMsgService.pushWechatTemplateMsg(nowUserTechAuth.getUserId(), WechatTemplateMsgEnum.TECH_AUTH_AUDIT_ING, user.getNickname(), requireCount.toString());
        }
        ApproveVO responseVO = new ApproveVO();
        BeanUtil.copyProperties(approve, responseVO);
        responseVO.setTechStatus(techStatus);
        responseVO.setApproveCount(approveCount);
        responseVO.setRequireCount(requireCount);
        return responseVO;
    }

    @Override
    public List<Approve> findByParam(ApproveVO approveVO) {
        return approveDao.findByParameter(approveVO);
    }

    @Override
    public void delByTechAuthId(Integer techAuthId){
        approveDao.delByTechAuthId(techAuthId);

    }

}
