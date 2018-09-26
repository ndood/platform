package com.fulu.game.core.service;

import com.fulu.game.core.entity.AssignOrderSetting;
import com.fulu.game.core.entity.vo.AssignOrderSettingVO;

import java.util.List;


/**
 * 派单设置
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-09-20 18:43:50
 */
public interface AssignOrderSettingService extends ICommonService<AssignOrderSetting,Integer>{


     void save(AssignOrderSettingVO assignOrderSettingVO);

     AssignOrderSettingVO findByUserId(Integer userId);
}
