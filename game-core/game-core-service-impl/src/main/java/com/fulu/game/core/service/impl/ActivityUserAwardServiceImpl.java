package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.ActivityUserAwardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.ActivityUserAwardDao;
import com.fulu.game.core.entity.ActivityUserAward;
import com.fulu.game.core.service.ActivityUserAwardService;

import java.util.List;


@Service
public class ActivityUserAwardServiceImpl extends AbsCommonService<ActivityUserAward,Integer> implements ActivityUserAwardService {

    @Autowired
	private ActivityUserAwardDao activityUserAwardDao;

    @Override
    public ICommonDao<ActivityUserAward, Integer> getDao() {
        return activityUserAwardDao;
    }



    @Override
    public ActivityUserAward findByUserAndActivity(Integer userId, Integer activityId) {
        ActivityUserAwardVO param = new ActivityUserAwardVO();
        param.setActivityId(activityId);
        param.setUserId(userId);
        List<ActivityUserAward> list = activityUserAwardDao.findByParameter(param);
        if(list.isEmpty()){
            return null;
        }
        return list.get(0);
    }
}
