package com.fulu.game.core.service.impl;


import com.fulu.game.common.Constant;
import com.fulu.game.common.exception.ParamsException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.UserWechatGroupShareDao;
import com.fulu.game.core.entity.RegistSource;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserWechatGroupShare;
import com.fulu.game.core.entity.vo.UserWechatGroupShareVO;
import com.fulu.game.core.service.RegistSourceService;
import com.fulu.game.core.service.UserWechatGroupShareService;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserWechatGroupShareServiceImpl extends AbsCommonService<UserWechatGroupShare, Integer>
        implements UserWechatGroupShareService {

    private final UserWechatGroupShareDao userWechatGroupShareDao;
    private final RegistSourceService registSourceService;

    @Autowired
    public UserWechatGroupShareServiceImpl(UserWechatGroupShareDao userWechatGroupShareDao,
                                           RegistSourceService registSourceService) {
        this.userWechatGroupShareDao = userWechatGroupShareDao;
        this.registSourceService = registSourceService;
    }

    @Override
    public ICommonDao<UserWechatGroupShare, Integer> getDao() {
        return userWechatGroupShareDao;
    }

    @Override
    public UserWechatGroupShare getUserShareStatus(User user) {
        if (user == null) {
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }
        Integer userId = user.getId();
        if (userId == null) {
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }
        UserWechatGroupShareVO groupShareVO = userWechatGroupShareDao.findByUserId(userId);
        RegistSource registSource = registSourceService.findCjRegistSource();
        Integer sourceId = Constant.CJ_SOURCE_ID;
        if (registSource != null) {
            sourceId = registSource.getId();
        }

        if (groupShareVO != null) {
            groupShareVO.setSourceId(sourceId);
            return groupShareVO;
        }

        UserWechatGroupShareVO resultGroupShareVO = new UserWechatGroupShareVO();
        resultGroupShareVO.setUserId(userId);
        resultGroupShareVO.setShareStatus(Constant.WECHAT_GROUP_SHARE_NOT_FINISHED);
        resultGroupShareVO.setGroupCounts(0);
        resultGroupShareVO.setSourceId(sourceId);

        UserWechatGroupShare paramGroupShare = new UserWechatGroupShare();
        BeanUtil.copyProperties(resultGroupShareVO, paramGroupShare);
        paramGroupShare.setUpdateTime(DateUtil.date());
        paramGroupShare.setCreateTime(DateUtil.date());
        userWechatGroupShareDao.create(paramGroupShare);
        return resultGroupShareVO;
    }
}
