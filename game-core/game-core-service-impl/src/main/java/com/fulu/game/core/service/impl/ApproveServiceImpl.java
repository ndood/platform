package com.fulu.game.core.service.impl;

import com.fulu.game.common.Constant;
import com.fulu.game.common.exception.ApproveException;
import com.fulu.game.common.exception.UserException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ApproveServiceImpl extends AbsCommonService<Approve, Integer> implements ApproveService {

    @Autowired
    private UserService userService;
    @Autowired
    private UserTechAuthService utaService;
    @Autowired
    private ApproveDao approveDao;

    @Override
    public ICommonDao<Approve, Integer> getDao() {
        return approveDao;
    }

    @Override
    public Approve save(Integer techAuthId) {
        User user = userService.getCurrentUser();
        Approve approve = new Approve();
        UserTechAuth userTechAuth = utaService.findById(techAuthId);
        if (null == userTechAuth) {
            throw new UserException(UserException.ExceptionCode.TECH_AUTH_NOT_EXIST_EXCEPTION);
        }
        Integer techOwnerId = userTechAuth.getUserId();
        ApproveVO approveVO = new ApproveVO();
        approveVO.setTechOwnerId(techOwnerId);
        approveVO.setUserId(user.getId());
        List<Approve> list = approveDao.findByParameter(approveVO);
        if (!CollectionUtil.isEmpty(list)) {
            throw new ApproveException(ApproveException.ExceptionCode.APPROVE_DUPLICATE);
        }
        approve.setTechAuthId(techAuthId);
        approve.setUserId(user.getId());
        approve.setTechOwnerId(techOwnerId);
        approve.setCreateTime(new Date());
        approveDao.create(approve);

        Integer currentCount = userTechAuth.getApproveCount();
        Integer newApproveCount = currentCount + 1;
        userTechAuth.setApproveCount(newApproveCount);
        if (newApproveCount >= Constant.DEFAULT_APPROVE_COUNT) {
            //todo 后期技能认证的状态需要同步改掉
            userTechAuth.setStatus(true);
        }
        utaService.update(userTechAuth);
        return approve;
    }

}
