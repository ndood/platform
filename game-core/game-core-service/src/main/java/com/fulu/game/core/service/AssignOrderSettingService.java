package com.fulu.game.core.service;

import com.fulu.game.core.entity.AssignOrderSetting;
import com.fulu.game.core.entity.vo.AssignOrderSettingVO;

import java.util.List;
import java.util.Set;


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


     /**
      * 通过游戏ID查询所有打开派单的用户ID
      * @return
      */
     Set<Integer> findOpenAssignUserByCategoryId(Integer categoryId);

}
