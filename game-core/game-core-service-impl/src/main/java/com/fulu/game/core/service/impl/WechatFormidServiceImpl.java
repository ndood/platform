package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.WechatFormidDao;
import com.fulu.game.core.entity.WechatFormid;
import com.fulu.game.core.entity.vo.WechatFormidVO;
import com.fulu.game.core.service.WechatFormidService;
import com.xiaoleilu.hutool.date.DateUnit;
import com.xiaoleilu.hutool.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class WechatFormidServiceImpl extends AbsCommonService<WechatFormid,Integer> implements WechatFormidService {

    @Autowired
	private WechatFormidDao wechatFormidDao;


    @Override
    public ICommonDao<WechatFormid, Integer> getDao() {
        return wechatFormidDao;
    }


    @Override
    public List<WechatFormid> findInSevenDaysFormIdByUser(Integer userId) {
        WechatFormidVO params = new WechatFormidVO();
        params.setUserId(userId);
        List<WechatFormid> formids = wechatFormidDao.findByParameter(params);
        List<WechatFormid> availableFormIds = new ArrayList<>();
        List<WechatFormid> notAvailableFormIds = new ArrayList<>();
        for (WechatFormid formid : formids) {
            long day = DateUtil.between(formid.getCreateTime(), new Date(), DateUnit.DAY);
            if (day < 7) {
                availableFormIds.add(formid);
            }else{
                notAvailableFormIds.add(formid);
            }
        }
        if(notAvailableFormIds.size()>0){
            deleteNotAvailableFormIds(notAvailableFormIds.toArray(new WechatFormid[]{}));
        }
        return availableFormIds;
    }


    public void deleteNotAvailableFormIds(WechatFormid ... wechatFormid){
        if(wechatFormid.length>0){
           new Thread(new Runnable() {
               @Override
               public void run() {
                   for(WechatFormid w:wechatFormid){
                       deleteById(w.getId());
                   }
               }
           }).start();
        }
    }

}
