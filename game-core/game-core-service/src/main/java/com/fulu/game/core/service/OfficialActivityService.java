package com.fulu.game.core.service;

import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.core.entity.OfficialActivity;
import com.fulu.game.core.entity.vo.OfficialActivityVO;

import java.util.List;


/**
 * 官方公告
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-09-28 14:32:06
 */
public interface OfficialActivityService extends ICommonService<OfficialActivity,Integer>{


     OfficialActivity create(int type, OfficialActivity activity, List<String> redeemCodes);

     void delete(int activityId);

     OfficialActivityVO getUsableActivity(PlatformEcoEnum platformEcoEnum);

     Boolean userJoinActivity(Integer userId,Integer activityId,String userIp);
}
