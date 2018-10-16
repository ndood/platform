package com.fulu.game.core.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.utils.DateUtils;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.AssignOrderSettingVO;
import com.fulu.game.core.service.UserTechAuthService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.AssignOrderSettingDao;
import com.fulu.game.core.entity.AssignOrderSetting;
import com.fulu.game.core.service.AssignOrderSettingService;

import java.util.*;


@Service
public class AssignOrderSettingServiceImpl extends AbsCommonService<AssignOrderSetting,Integer> implements AssignOrderSettingService {

    @Autowired
	private AssignOrderSettingDao assignOrderSettingDao;

    @Autowired
    private UserTechAuthServiceImpl userTechAuthServiceImpl;

    @Override
    public ICommonDao<AssignOrderSetting, Integer> getDao() {
        return assignOrderSettingDao;
    }


    @Override
    public void save(AssignOrderSettingVO assignOrderSettingVO) {
        if(assignOrderSettingVO.getId()==null){
            if(assignOrderSettingVO.getEnable()==null){
                assignOrderSettingVO.setEnable(false);
            }
            assignOrderSettingVO.setCreateTime(new Date());
            assignOrderSettingVO.setUpdateTime(new Date());
            create(assignOrderSettingVO);
        }else{
            assignOrderSettingVO.setUpdateTime(new Date());
            update(assignOrderSettingVO);
        }
    }

    @Override
    public AssignOrderSettingVO findByUserId(Integer userId) {
        AssignOrderSettingVO param = new AssignOrderSettingVO();
        param.setUserId(userId);
        List<AssignOrderSetting> list = assignOrderSettingDao.findByParameter(param);
        if(list.isEmpty()){
            return null;
        }
        AssignOrderSettingVO result = new AssignOrderSettingVO();
        BeanUtil.copyProperties(list.get(0),result);
        return result;
    }





    @Override
    public Set<Integer> findOpenAssignUserByCategoryId(Integer categoryId) {
        Set<Integer> userIds = new HashSet<>();
        List<UserTechAuth> userTechAuths = userTechAuthServiceImpl.findNormalByCategory(categoryId);
        for(UserTechAuth userTechAuth : userTechAuths){
            userIds.add(userTechAuth.getUserId());
        }
        List<AssignOrderSetting> assignOrderSettings =  assignOrderSettingDao.findEnableByUserIds(new ArrayList<>(userIds));
        //过滤用户未设置的派单
        for(AssignOrderSetting assignOrderSetting : assignOrderSettings){
            AssignOrderSettingVO assignOrderSettingVO = new AssignOrderSettingVO();
            BeanUtil.copyProperties(assignOrderSetting,assignOrderSettingVO);
            Integer[] weekDays =  assignOrderSettingVO.getWeekDays();
            int thisDayOfWeek = DateUtil.thisDayOfWeek();
            if(thisDayOfWeek==1){
                thisDayOfWeek = 7;
            }else{
                thisDayOfWeek+=1;
            }
            //匹配周是否匹配
            if(!ArrayUtils.contains(weekDays,thisDayOfWeek)){
                userIds.remove(assignOrderSetting.getUserId());
            }
            //匹配时间是否匹配
            if(StringUtils.isBlank(assignOrderSetting.getBeginTime())||StringUtils.isBlank(assignOrderSetting.getEndTime())){
                continue;
            }
            //匹配时间是否匹配
            Date beginDate = DateUtil.parseTime(assignOrderSetting.getBeginTime()+":00");
            Date endDate = DateUtil.parseTime(assignOrderSetting.getEndTime()+":00");
            Date currentDate = DateUtil.parseTime(DateUtil.format(new Date(),"HH:mm:ss"));
            if(currentDate.compareTo(beginDate)<0||currentDate.compareTo(endDate)>0){
                userIds.remove(assignOrderSetting.getUserId());
            }
        }
        return userIds;
    }


}
