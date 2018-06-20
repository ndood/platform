package com.fulu.game.core.service.impl;


import com.fulu.game.common.threadpool.SpringThreadPoolExecutor;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.WechatFormidDao;
import com.fulu.game.core.entity.WechatFormid;
import com.fulu.game.core.entity.vo.WechatFormidVO;
import com.fulu.game.core.service.WechatFormidService;
import com.github.pagehelper.PageHelper;
import com.xiaoleilu.hutool.date.DateField;
import com.xiaoleilu.hutool.date.DateUnit;
import com.xiaoleilu.hutool.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class WechatFormidServiceImpl extends AbsCommonService<WechatFormid, Integer> implements WechatFormidService {

    @Autowired
    private WechatFormidDao wechatFormidDao;
    @Autowired
    private SpringThreadPoolExecutor springThreadPoolExecutor;

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
            } else {
                notAvailableFormIds.add(formid);
            }
        }
        if (notAvailableFormIds.size() > 0) {
            springThreadPoolExecutor.getAsyncExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    deleteNotAvailableFormIds(notAvailableFormIds.toArray(new WechatFormid[]{}));
                }
            });
        }
        return availableFormIds;
    }


    public void deleteNotAvailableFormIds(WechatFormid... wechatFormid) {
        if (wechatFormid.length > 0) {
            for (WechatFormid w : wechatFormid) {
                log.info("删除formId:wechatFormid:{}",wechatFormid);
                deleteById(w.getId());
            }
        }
    }

    @Override
    public List<WechatFormidVO> findByUserId(List<Integer> userIds) {
        //删除表里面过期的formId
        Date date = DateUtil.offset(new Date(), DateField.HOUR,(-24*7)+1);
        wechatFormidDao.deleteByExpireTime(date);
        List<WechatFormidVO> result = new ArrayList<>();
        for(int i=0;;i++){
            PageHelper.startPage(0,100);
            List<WechatFormidVO> wechatFormidVOS = wechatFormidDao.findByUserIds(userIds);
            result.addAll(wechatFormidVOS);
            if(wechatFormidVOS.isEmpty()){
                break;
            }
        }
        return result;
    }

}
