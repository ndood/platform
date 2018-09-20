package com.fulu.game.core.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.AssignOrderSettingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.AssignOrderSettingDao;
import com.fulu.game.core.entity.AssignOrderSetting;
import com.fulu.game.core.service.AssignOrderSettingService;

import java.util.Date;
import java.util.List;


@Service
public class AssignOrderSettingServiceImpl extends AbsCommonService<AssignOrderSetting,Integer> implements AssignOrderSettingService {

    @Autowired
	private AssignOrderSettingDao assignOrderSettingDao;


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


}
