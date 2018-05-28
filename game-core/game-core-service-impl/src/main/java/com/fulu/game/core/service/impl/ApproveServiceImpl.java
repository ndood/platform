package com.fulu.game.core.service.impl;

import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.TechAuthStatusEnum;
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
    private ApproveDao approveDao;

    @Override
    public ICommonDao<Approve, Integer> getDao() {
        return approveDao;
    }

    @Override
    public Approve save(Integer techAuthId) {
        log.info("====好友认可接口执行====入参技能id:{},时间:{}", techAuthId, TimeUtil.defaultFormat(new Date()));
        UserTechAuth userTechAuth = utaService.findById(techAuthId);
        if (null == userTechAuth) {
            throw new UserException(UserException.ExceptionCode.TECH_AUTH_NOT_EXIST_EXCEPTION);
        }
        if (userTechAuth.getStatus() == TechAuthStatusEnum.FREEZE.getType()) {
            throw new ApproveException(ApproveException.ExceptionCode.APPROVE_FREEZE);
        }
        ApproveVO approveVO = new ApproveVO();
        Integer techOwnerId = userTechAuth.getUserId();
        log.info("技能申请者id:{}", techOwnerId);
        approveVO.setTechOwnerId(techOwnerId);
        User user = userService.getCurrentUser();
        log.info("认可人id:{}", user.getId());
        approveVO.setUserId(user.getId());
        List<Approve> list = approveDao.findByParameter(approveVO);
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
        return approve;
    }

    @Override
    public List<Approve> findByParam(ApproveVO approveVO) {
        return approveDao.findByParameter(approveVO);
    }

}
